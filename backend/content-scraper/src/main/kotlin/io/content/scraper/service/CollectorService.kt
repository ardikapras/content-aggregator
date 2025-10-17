package io.content.scraper.service

import io.content.scraper.enums.ActivityLogAction
import io.content.scraper.enums.ActivityLogStatus
import io.content.scraper.enums.ArticleStatus
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.repository.SourceRepository
import io.content.scraper.temporal.workflow.ArticleProcessingRequest
import io.content.scraper.temporal.workflow.ArticleProcessingWorkflow
import io.content.scraper.temporal.workflow.DailyAggregationResult
import io.content.scraper.temporal.workflow.DailyAggregationWorkflow
import io.github.oshai.kotlinlogging.KotlinLogging
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class CollectorService(
    private val sourceRepository: SourceRepository,
    private val articleRepository: ArticleRepository,
    private val activityLogService: ActivityLogService,
    private val applicationScope: CoroutineScope,
    private val workflowClient: WorkflowClient,
    private val metricsService: MetricsService,
    @Value("\${app.max-retry-count:3}") private val maxRetryCount: Int,
) {
    private val logger = KotlinLogging.logger {}

    suspend fun start(): Map<String, Int> =
        withContext(applicationScope.coroutineContext) {
            logger.debug { "Starting daily aggregation with hierarchical Temporal workflows" }

            try {
                val workflowOptions =
                    WorkflowOptions
                        .newBuilder()
                        .setWorkflowId("daily-aggregation-${System.currentTimeMillis()}")
                        .setTaskQueue("DAILY_AGGREGATION_TASK_QUEUE")
                        .setWorkflowExecutionTimeout(Duration.ofHours(2))
                        .build()

                val workflow = workflowClient.newWorkflowStub(DailyAggregationWorkflow::class.java, workflowOptions)
                metricsService.recordTemporalWorkflowExecuted("DailyAggregation", "started")

                val result: DailyAggregationResult = workflow.runDailyAggregation()
                metricsService.recordTemporalWorkflowExecuted("DailyAggregation", "completed")

                val resultsBySource = result.sourcesResults.mapValues { it.value.articlesProcessed }

                // Update metrics with aggregation results
                metricsService.updateActiveSourcesCount(result.activeSources.toLong())
                result.sourcesResults.forEach { (sourceName, sourceResult) ->
                    repeat(sourceResult.articlesProcessed) {
                        metricsService.recordArticleProcessed(sourceName)
                    }
                    repeat(sourceResult.articlesFailed) {
                        metricsService.recordArticleFailed(sourceName, "aggregation_failed")
                    }
                    metricsService.recordSourceScraped(sourceName)
                }

                activityLogService.logActivity(
                    ActivityLogAction.SCRAPE.name,
                    ActivityLogStatus.SUCCESS.name,
                    "Daily aggregation completed: ${result.activeSources} sources, ${result.totalArticlesProcessed} articles processed, ${result.totalArticlesSuccessful} successful, ${result.totalArticlesFailed} failed",
                )

                val timeText =
                    if (result.executionTimeMinutes < 2) {
                        "under 1 minute"
                    } else {
                        "${result.executionTimeMinutes} minutes"
                    }

                logger.info {
                    "Daily aggregation completed: ${result.totalArticlesProcessed} articles processed in $timeText"
                }
                resultsBySource
            } catch (e: Exception) {
                logger.error(e) { "Error starting daily aggregation workflow" }
                metricsService.recordTemporalWorkflowExecuted("DailyAggregation", "failed")

                activityLogService.logActivity(
                    ActivityLogAction.SCRAPE.name,
                    ActivityLogStatus.FAILED.name,
                    "Failed to start daily aggregation: ${e.message}",
                )

                throw e
            }
        }

    /**
     * Retry articles with DISCOVERED status and less than max retry count using Temporal workflows
     * Returns a map of source names to article counts
     */
    fun retryPendingArticles(): Map<String, Int> {
        val pendingArticles =
            articleRepository.findByStatusInAndRetryCountLessThan(
                listOf(ArticleStatus.DISCOVERED.name, ArticleStatus.ERROR_SCRAPE.name),
                maxRetryCount,
            )

        val countBySource = mutableMapOf<String, Int>()

        try {
            pendingArticles.forEach { article ->
                val sourceName = article.source.name
                countBySource[sourceName] = countBySource.getOrDefault(sourceName, 0) + 1

                val parserConfig = article.source.parserConfig
                val parsingStrategy = parserConfig?.name ?: article.source.parsingStrategy

                val workflowOptions =
                    WorkflowOptions
                        .newBuilder()
                        .setWorkflowId("retry-article-${article.id}")
                        .setTaskQueue("ARTICLE_PROCESSING_TASK_QUEUE")
                        .setWorkflowExecutionTimeout(Duration.ofMinutes(10))
                        .build()

                val request =
                    ArticleProcessingRequest(
                        articleId = article.id,
                        url = article.url,
                        parsingStrategy = parsingStrategy,
                        sourceId = article.source.id,
                    )

                val workflow = workflowClient.newWorkflowStub(ArticleProcessingWorkflow::class.java, workflowOptions)
                // Start workflow asynchronously
                WorkflowClient.start(workflow::processArticle, request)

                logger.debug { "Started retry workflow for article ${article.id}" }
            }

            activityLogService.logActivity(
                ActivityLogAction.RETRY_SCRAPE.name,
                ActivityLogStatus.SUCCESS.name,
                "Successfully started retry workflows for ${pendingArticles.size} pending articles",
            )
        } catch (e: Exception) {
            logger.error(e) { "Error retrying pending articles" }

            activityLogService.logActivity(
                ActivityLogAction.RETRY_SCRAPE.name,
                ActivityLogStatus.FAILED.name,
                "Failed to retry pending articles: ${e.message}",
            )

            throw e
        }

        return countBySource
    }
}
