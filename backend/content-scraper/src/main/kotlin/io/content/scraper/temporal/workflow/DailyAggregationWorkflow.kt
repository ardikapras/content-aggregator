package io.content.scraper.temporal.workflow

import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface DailyAggregationWorkflow {
    // Version 1.0 - Initial implementation
    @WorkflowMethod(name = "DailyAggregationWorkflow")
    fun runDailyAggregation(): DailyAggregationResult
}

data class DailyAggregationResult(
    val totalSources: Int = 0,
    val activeSources: Int = 0,
    val totalArticlesProcessed: Int = 0,
    val totalArticlesSuccessful: Int = 0,
    val totalArticlesFailed: Int = 0,
    val executionTimeMinutes: Long = 0,
    val sourcesResults: Map<String, SourceCollectionResult> = emptyMap(),
)
