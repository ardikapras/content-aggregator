package io.content.scraper.temporal.workflow

import io.temporal.workflow.QueryMethod
import io.temporal.workflow.SignalMethod
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import java.util.UUID

@WorkflowInterface
interface ArticleProcessingWorkflow {
    // Version 1.0 - Initial implementation
    @WorkflowMethod(name = "ArticleProcessingWorkflow")
    fun processArticle(request: ArticleProcessingRequest): ArticleProcessingResult

    @QueryMethod
    fun getProgress(): ArticleProcessingProgress

    @SignalMethod
    fun cancelProcessing()
}

data class ArticleProcessingRequest(
    val articleId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
    val url: String = "",
    val parsingStrategy: String = "",
    val sourceId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
)

data class ArticleProcessingResult(
    val articleId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
    val success: Boolean = false,
    val content: String? = null,
    val author: String? = null,
    val errorMessage: String? = null,
    val retryCount: Int = 0,
)

data class ArticleProcessingProgress(
    val articleId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
    val status: String = "INITIALIZING",
    val currentStep: String = "",
    val progress: Int = 0,
    val totalSteps: Int = 100,
)
