package io.content.scraper.temporal.workflow.impl

import io.content.scraper.temporal.activity.AnalyticsActivity
import io.content.scraper.temporal.activity.DataActivity
import io.content.scraper.temporal.workflow.DailyAggregationResult
import io.content.scraper.temporal.workflow.DailyAggregationWorkflow
import io.content.scraper.temporal.workflow.SourceCollectionWorkflow
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.workflow.Async
import io.temporal.workflow.ChildWorkflowOptions
import io.temporal.workflow.Workflow
import java.time.Duration

class DailyAggregationWorkflowImpl : DailyAggregationWorkflow {
    private val dataActivity =
        Workflow.newActivityStub(
            DataActivity::class.java,
            ActivityOptions
                .newBuilder()
                .setScheduleToCloseTimeout(Duration.ofMinutes(5))
                .setRetryOptions(
                    RetryOptions
                        .newBuilder()
                        .setMaximumAttempts(3)
                        .build(),
                ).build(),
        )

    private val analyticsActivity =
        Workflow.newActivityStub(
            AnalyticsActivity::class.java,
            ActivityOptions
                .newBuilder()
                .setScheduleToCloseTimeout(Duration.ofMinutes(2))
                .build(),
        )

    override fun runDailyAggregation(): DailyAggregationResult {
        val startTime = Workflow.currentTimeMillis()

        // Get all active sources
        val activeSources = dataActivity.findAllActiveSources()

        // Spawn child workflows for each source in parallel
        val sourceWorkflowPromises =
            activeSources.map { source ->
                val childWorkflowOptions =
                    ChildWorkflowOptions
                        .newBuilder()
                        .setWorkflowId("source-collection-${source.id}-${Workflow.currentTimeMillis()}")
                        .setTaskQueue("SOURCE_COLLECTION_TASK_QUEUE")
                        .setWorkflowExecutionTimeout(Duration.ofMinutes(30))
                        .build()

                val sourceWorkflow =
                    Workflow.newChildWorkflowStub(
                        SourceCollectionWorkflow::class.java,
                        childWorkflowOptions,
                    )

                Async.function { sourceWorkflow.collectFromSource(source.id) }
            }

        // Wait for all source workflows to complete
        val sourceResults = sourceWorkflowPromises.map { it.get() }

        // Create results map
        val resultsMap = sourceResults.associateBy { it.sourceName }

        // Generate summary analytics
        analyticsActivity.generateDailyAggregationSummary(resultsMap)

        val endTime = Workflow.currentTimeMillis()
        val executionTimeMs = endTime - startTime
        val executionTimeMinutes =
            if (executionTimeMs < 60000) {
                // If less than 1 minute, round up to 1 minute for display purposes
                1L
            } else {
                executionTimeMs / 60000
            }

        return DailyAggregationResult(
            totalSources = activeSources.size,
            activeSources = activeSources.size,
            totalArticlesProcessed = sourceResults.sumOf { it.articlesProcessed },
            totalArticlesSuccessful = sourceResults.sumOf { it.articlesSuccessful },
            totalArticlesFailed = sourceResults.sumOf { it.articlesFailed },
            executionTimeMinutes = executionTimeMinutes,
            sourcesResults = resultsMap,
        )
    }
}
