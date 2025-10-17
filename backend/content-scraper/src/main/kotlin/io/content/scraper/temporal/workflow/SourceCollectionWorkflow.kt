package io.content.scraper.temporal.workflow

import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import java.util.UUID

@WorkflowInterface
interface SourceCollectionWorkflow {
    // Version 1.0 - Initial implementation
    @WorkflowMethod(name = "SourceCollectionWorkflow")
    fun collectFromSource(sourceId: UUID): SourceCollectionResult
}

data class SourceCollectionResult(
    val sourceId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
    val sourceName: String = "",
    val articlesDiscovered: Int = 0,
    val articlesProcessed: Int = 0,
    val articlesSuccessful: Int = 0,
    val articlesFailed: Int = 0,
    val feedFetchSuccess: Boolean = false,
    val executionTimeMinutes: Long = 0,
    val errorMessage: String? = null,
)
