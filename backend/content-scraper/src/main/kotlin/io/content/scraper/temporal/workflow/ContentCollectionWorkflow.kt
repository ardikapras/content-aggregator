package io.content.scraper.temporal.workflow

import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import java.util.UUID

@WorkflowInterface
interface ContentCollectionWorkflow {
    // Version 1.0 - Initial implementation
    @WorkflowMethod(name = "ContentCollectionWorkflow")
    fun collectContentFromSource(sourceId: UUID): ContentCollectionResult
}

data class ContentCollectionResult(
    val sourceId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
    val sourceName: String = "",
    val articlesProcessed: Int = 0,
    val articlesSuccessful: Int = 0,
    val articlesFailed: Int = 0,
)
