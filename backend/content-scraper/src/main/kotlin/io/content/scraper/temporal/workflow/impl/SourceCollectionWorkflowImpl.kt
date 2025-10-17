package io.content.scraper.temporal.workflow.impl

import io.content.scraper.temporal.activity.DataActivity
import io.content.scraper.temporal.activity.FeedActivity
import io.content.scraper.temporal.types.NewArticleData
import io.content.scraper.temporal.workflow.ArticleProcessingRequest
import io.content.scraper.temporal.workflow.ArticleProcessingWorkflow
import io.content.scraper.temporal.workflow.SourceCollectionResult
import io.content.scraper.temporal.workflow.SourceCollectionWorkflow
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.workflow.Async
import io.temporal.workflow.ChildWorkflowOptions
import io.temporal.workflow.Workflow
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

class SourceCollectionWorkflowImpl : SourceCollectionWorkflow {
    private val feedActivity =
        Workflow.newActivityStub(
            FeedActivity::class.java,
            ActivityOptions
                .newBuilder()
                .setScheduleToCloseTimeout(Duration.ofMinutes(5))
                .setRetryOptions(
                    RetryOptions
                        .newBuilder()
                        .setMaximumAttempts(3)
                        .setInitialInterval(Duration.ofSeconds(10))
                        .setBackoffCoefficient(2.0)
                        .build(),
                ).build(),
        )

    private val dataActivity =
        Workflow.newActivityStub(
            DataActivity::class.java,
            ActivityOptions
                .newBuilder()
                .setScheduleToCloseTimeout(Duration.ofMinutes(3))
                .setRetryOptions(
                    RetryOptions
                        .newBuilder()
                        .setMaximumAttempts(3)
                        .build(),
                ).build(),
        )

    override fun collectFromSource(sourceId: UUID): SourceCollectionResult {
        val startTime = Workflow.currentTimeMillis()

        // Get source information
        val source =
            dataActivity.findSourceById(sourceId)
                ?: return SourceCollectionResult(
                    sourceId = sourceId,
                    sourceName = "Unknown Source",
                    errorMessage = "Source not found",
                )

        try {
            // Fetch RSS feed
            val feedResult = feedActivity.fetchRSSFeed(sourceId)
            if (!feedResult.success) {
                return SourceCollectionResult(
                    sourceId = sourceId,
                    sourceName = source.name,
                    feedFetchSuccess = false,
                    errorMessage = feedResult.errorMessage,
                )
            }

            // Extract article URLs from feed
            val discoveredArticles =
                feedActivity.extractArticleUrls(
                    feedResult.feedContent ?: "",
                    source.url,
                )

            // Filter out existing articles
            val newArticles =
                discoveredArticles.filter { article ->
                    !dataActivity.articleExistsByUrl(article.url)
                }

            if (newArticles.isEmpty()) {
                dataActivity.updateSourceLastScraped(sourceId, LocalDateTime.now())
                return SourceCollectionResult(
                    sourceId = sourceId,
                    sourceName = source.name,
                    articlesDiscovered = discoveredArticles.size,
                    articlesProcessed = 0,
                    feedFetchSuccess = true,
                )
            }

            // Save new articles to database
            val newArticleData =
                newArticles.map { article ->
                    NewArticleData(
                        title = article.title,
                        url = article.url,
                        description = article.description,
                        publishDate = article.publishDate,
                    )
                }

            val savedArticleIds = dataActivity.saveNewArticles(newArticleData, sourceId)

            // Spawn child workflows for each new article in parallel
            val parserConfig = source.parserConfig
            val parsingStrategy = parserConfig?.name ?: source.parsingStrategy

            val articleProcessingPromises =
                savedArticleIds.map { articleId ->
                    val childWorkflowOptions =
                        ChildWorkflowOptions
                            .newBuilder()
                            .setWorkflowId("article-processing-$articleId")
                            .setTaskQueue("ARTICLE_PROCESSING_TASK_QUEUE")
                            .setWorkflowExecutionTimeout(Duration.ofMinutes(10))
                            .build()

                    val articleWorkflow =
                        Workflow.newChildWorkflowStub(
                            ArticleProcessingWorkflow::class.java,
                            childWorkflowOptions,
                        )

                    val request =
                        ArticleProcessingRequest(
                            articleId = articleId,
                            url = newArticles[savedArticleIds.indexOf(articleId)].url,
                            parsingStrategy = parsingStrategy,
                            sourceId = sourceId,
                        )

                    Async.function { articleWorkflow.processArticle(request) }
                }

            // Wait for all article processing to complete
            val articleResults = articleProcessingPromises.map { it.get() }

            // Update source last scraped timestamp
            dataActivity.updateSourceLastScraped(sourceId, LocalDateTime.now())

            val endTime = Workflow.currentTimeMillis()
            val executionTimeMinutes = (endTime - startTime) / 60000

            return SourceCollectionResult(
                sourceId = sourceId,
                sourceName = source.name,
                articlesDiscovered = discoveredArticles.size,
                articlesProcessed = articleResults.size,
                articlesSuccessful = articleResults.count { it.success },
                articlesFailed = articleResults.count { !it.success },
                feedFetchSuccess = true,
                executionTimeMinutes = executionTimeMinutes,
            )
        } catch (e: Exception) {
            val endTime = Workflow.currentTimeMillis()
            val executionTimeMinutes = (endTime - startTime) / 60000

            return SourceCollectionResult(
                sourceId = sourceId,
                sourceName = source.name,
                feedFetchSuccess = false,
                executionTimeMinutes = executionTimeMinutes,
                errorMessage = e.message,
            )
        }
    }
}
