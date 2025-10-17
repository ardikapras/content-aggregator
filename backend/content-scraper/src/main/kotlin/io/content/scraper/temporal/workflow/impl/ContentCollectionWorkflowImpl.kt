package io.content.scraper.temporal.workflow.impl

import io.content.scraper.enums.ActivityLogAction
import io.content.scraper.enums.ActivityLogStatus
import io.content.scraper.temporal.activity.ActivityLogActivity
import io.content.scraper.temporal.activity.DataActivity
import io.content.scraper.temporal.activity.RSSFeedActivity
import io.content.scraper.temporal.workflow.ArticleProcessingRequest
import io.content.scraper.temporal.workflow.ArticleProcessingWorkflow
import io.content.scraper.temporal.workflow.ContentCollectionResult
import io.content.scraper.temporal.workflow.ContentCollectionWorkflow
import io.temporal.activity.ActivityOptions
import io.temporal.workflow.Async
import io.temporal.workflow.ChildWorkflowOptions
import io.temporal.workflow.Promise
import io.temporal.workflow.Workflow
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

class ContentCollectionWorkflowImpl : ContentCollectionWorkflow {
    private val activityOptions =
        ActivityOptions
            .newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(5))
            .setRetryOptions(
                io.temporal.common.RetryOptions
                    .newBuilder()
                    .setMaximumAttempts(3)
                    .setInitialInterval(Duration.ofSeconds(1))
                    .setMaximumInterval(Duration.ofMinutes(1))
                    .setBackoffCoefficient(2.0)
                    .build(),
            ).build()

    private val childWorkflowOptions =
        ChildWorkflowOptions
            .newBuilder()
            .setTaskQueue("ARTICLE_PROCESSING_TASK_QUEUE")
            .build()

    private val dataActivity = Workflow.newActivityStub(DataActivity::class.java, activityOptions)
    private val rssFeedActivity = Workflow.newActivityStub(RSSFeedActivity::class.java, activityOptions)
    private val activityLogActivity = Workflow.newActivityStub(ActivityLogActivity::class.java, activityOptions)

    override fun collectContentFromSource(sourceId: UUID): ContentCollectionResult {
        activityLogActivity.logWorkflowStarted("ContentCollection", "Starting content collection for source: $sourceId")

        val source =
            dataActivity.findSourceById(sourceId)
                ?: return ContentCollectionResult(
                    sourceId = sourceId,
                    sourceName = "Unknown",
                    articlesProcessed = 0,
                    articlesSuccessful = 0,
                    articlesFailed = 1,
                )

        val feedResult = rssFeedActivity.fetchRSSFeed(sourceId)
        if (!feedResult.success || feedResult.feedContent == null) {
            activityLogActivity.logActivity(
                ActivityLogAction.SCRAPE,
                ActivityLogStatus.FAILED,
                "Failed to fetch RSS feed for source: ${source.name}",
            )
            return ContentCollectionResult(
                sourceId = sourceId,
                sourceName = source.name,
                articlesProcessed = 0,
                articlesSuccessful = 0,
                articlesFailed = 1,
            )
        }

        val articleUrls = rssFeedActivity.extractArticleUrls(feedResult.feedContent, source.url)
        val newArticleIds =
            rssFeedActivity.saveNewArticles(
                articleUrls.map { urlInfo ->
                    io.content.scraper.temporal.types.NewArticleData(
                        title = urlInfo.title,
                        url = urlInfo.url,
                        description = urlInfo.description,
                        publishDate = urlInfo.publishDate,
                    )
                },
                sourceId,
            )

        val processingPromises = mutableListOf<Promise<io.content.scraper.temporal.workflow.ArticleProcessingResult>>()

        newArticleIds.forEach { articleId ->
            val correspondingUrl = articleUrls.find { it.url == articleUrls[newArticleIds.indexOf(articleId)].url }?.url ?: ""
            val parserConfig = source.parserConfig
            val parsingStrategy = parserConfig?.name ?: source.parsingStrategy

            val request =
                ArticleProcessingRequest(
                    articleId = articleId,
                    url = correspondingUrl,
                    parsingStrategy = parsingStrategy,
                    sourceId = sourceId,
                )

            val childWorkflow = Workflow.newChildWorkflowStub(ArticleProcessingWorkflow::class.java, childWorkflowOptions)
            processingPromises.add(Async.function(childWorkflow::processArticle, request))
        }

        var successfulCount = 0
        var failedCount = 0

        processingPromises.forEach { promise ->
            val result = promise.get()
            if (result.success) {
                successfulCount++
            } else {
                failedCount++
            }
        }

        rssFeedActivity.updateSourceLastScraped(sourceId, LocalDateTime.now())

        activityLogActivity.logWorkflowCompleted(
            "ContentCollection",
            failedCount == 0,
            "Processed ${newArticleIds.size} articles for source: ${source.name}. Success: $successfulCount, Failed: $failedCount",
        )

        return ContentCollectionResult(
            sourceId = sourceId,
            sourceName = source.name,
            articlesProcessed = newArticleIds.size,
            articlesSuccessful = successfulCount,
            articlesFailed = failedCount,
        )
    }
}
