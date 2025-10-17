package io.content.scraper.temporal.workflow.impl

import io.content.scraper.enums.ArticleStatus
import io.content.scraper.temporal.activity.ContentActivity
import io.content.scraper.temporal.activity.DataActivity
import io.content.scraper.temporal.types.ContentParsingRequest
import io.content.scraper.temporal.workflow.ArticleProcessingProgress
import io.content.scraper.temporal.workflow.ArticleProcessingRequest
import io.content.scraper.temporal.workflow.ArticleProcessingResult
import io.content.scraper.temporal.workflow.ArticleProcessingWorkflow
import io.temporal.activity.ActivityOptions
import io.temporal.failure.ApplicationFailure
import io.temporal.workflow.Workflow
import java.time.Duration

class ArticleProcessingWorkflowImpl : ArticleProcessingWorkflow {
    private var currentProgress = ArticleProcessingProgress()
    private var isCancelled = false
    private val defaultActivityOptions =
        ActivityOptions
            .newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(2))
            .setScheduleToCloseTimeout(Duration.ofMinutes(5))
            .setRetryOptions(
                io.temporal.common.RetryOptions
                    .newBuilder()
                    .setMaximumAttempts(3)
                    .setInitialInterval(Duration.ofSeconds(1))
                    .setMaximumInterval(Duration.ofSeconds(30))
                    .setBackoffCoefficient(2.0)
                    .build(),
            ).build()

    private val longRunningActivityOptions =
        ActivityOptions
            .newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(10))
            .setHeartbeatTimeout(Duration.ofSeconds(30))
            .setRetryOptions(
                io.temporal.common.RetryOptions
                    .newBuilder()
                    .setMaximumAttempts(2)
                    .setInitialInterval(Duration.ofSeconds(5))
                    .setMaximumInterval(Duration.ofMinutes(5))
                    .setBackoffCoefficient(2.0)
                    .build(),
            ).build()

    private val dataActivity = Workflow.newActivityStub(DataActivity::class.java, defaultActivityOptions)
    private val contentActivity = Workflow.newActivityStub(ContentActivity::class.java, longRunningActivityOptions)

    override fun processArticle(request: ArticleProcessingRequest): ArticleProcessingResult {
        currentProgress =
            ArticleProcessingProgress(
                articleId = request.articleId,
                status = "STARTING",
                currentStep = "Fetching article from database",
                progress = 10,
            )

        if (isCancelled) {
            return ArticleProcessingResult(
                articleId = request.articleId,
                success = false,
                errorMessage = "Processing was cancelled",
            )
        }

        val article =
            dataActivity.findArticleById(request.articleId)
                ?: throw ApplicationFailure.newFailure("Article not found: ${request.articleId}", "ARTICLE_NOT_FOUND")

        currentProgress =
            currentProgress.copy(
                status = "PARSING",
                currentStep = "Parsing article content",
                progress = 30,
            )

        return try {
            val parsingRequest =
                ContentParsingRequest(
                    articleId = request.articleId,
                    url = request.url,
                    parsingStrategy = request.parsingStrategy,
                )

            val parsingResult = contentActivity.parseArticleContent(parsingRequest)

            currentProgress =
                currentProgress.copy(
                    status = "PROCESSING",
                    currentStep = "Processing parsed content",
                    progress = 60,
                )

            if (parsingResult.success) {
                val wordCount = parsingResult.content?.split("\\s+".toRegex())?.size ?: 0
                val readingTimeMinutes = (wordCount / 200).coerceAtLeast(1)

                currentProgress =
                    currentProgress.copy(
                        status = "SAVING",
                        currentStep = "Saving article content to database",
                        progress = 90,
                    )

                dataActivity.updateArticleContent(
                    articleId = request.articleId,
                    content = parsingResult.content,
                    author = parsingResult.author,
                    wordCount = wordCount,
                    readingTimeMinutes = readingTimeMinutes,
                    status = ArticleStatus.SCRAPED.name,
                )

                currentProgress =
                    currentProgress.copy(
                        status = "COMPLETED",
                        currentStep = "Article processing completed successfully",
                        progress = 100,
                    )

                ArticleProcessingResult(
                    articleId = request.articleId,
                    success = true,
                    content = parsingResult.content,
                    author = parsingResult.author,
                    errorMessage = null,
                    retryCount = article.retryCount,
                )
            } else {
                handleParsingFailure(request, parsingResult.errorMessage ?: "Unknown parsing error", article.retryCount)
            }
        } catch (e: Exception) {
            handleParsingFailure(request, e.message ?: "Unknown error", article.retryCount)
        }
    }

    private fun handleParsingFailure(
        request: ArticleProcessingRequest,
        errorMessage: String,
        currentRetryCount: Int,
    ): ArticleProcessingResult {
        dataActivity.incrementArticleRetryCount(request.articleId)
        dataActivity.updateArticleContent(
            articleId = request.articleId,
            content = null,
            author = null,
            wordCount = 0,
            readingTimeMinutes = 0,
            status = ArticleStatus.ERROR_SCRAPE.name,
            errorMessage = errorMessage,
        )

        return ArticleProcessingResult(
            articleId = request.articleId,
            success = false,
            content = null,
            author = null,
            errorMessage = errorMessage,
            retryCount = currentRetryCount + 1,
        )
    }

    override fun getProgress(): ArticleProcessingProgress = currentProgress

    override fun cancelProcessing() {
        isCancelled = true
        currentProgress =
            currentProgress.copy(
                status = "CANCELLED",
                currentStep = "Processing was cancelled by user",
            )
    }
}
