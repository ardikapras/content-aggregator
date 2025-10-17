package io.content.scraper.temporal.types

import java.time.LocalDateTime
import java.util.UUID

// Common activity data types used across workflows and activities

data class NewArticleData(
    val title: String = "",
    val url: String = "",
    val description: String = "",
    val publishDate: LocalDateTime? = null,
)

data class MultiPageParsingRequest(
    val articleId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
    val initialUrl: String = "",
    val parsingStrategy: String = "",
    val maxPages: Int = 10,
)

data class ArticleDiscoveryInfo(
    val title: String = "",
    val url: String = "",
    val description: String = "",
    val publishDate: LocalDateTime? = null,
)

data class FeedFetchResult(
    val success: Boolean = false,
    val feedContent: String? = null,
    val errorMessage: String? = null,
    val responseTimeMs: Long = 0,
)

data class ContentParsingRequest(
    val articleId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
    val url: String = "",
    val parsingStrategy: String = "",
)

data class ContentParsingResult(
    val success: Boolean = false,
    val content: String? = null,
    val author: String? = null,
    val extractedPages: Int = 1,
    val errorMessage: String? = null,
)

data class ContentMetadata(
    val wordCount: Int = 0,
    val readingTimeMinutes: Int = 0,
    val language: String? = null,
    val tags: List<String> = emptyList(),
)

data class SourceStatistics(
    val totalArticles: Int = 0,
    val successfulArticles: Int = 0,
    val failedArticles: Int = 0,
    val averageProcessingTime: Long = 0,
)

data class AggregationSummary(
    val totalSources: Int = 0,
    val totalArticles: Int = 0,
    val successRate: Double = 0.0,
    val totalProcessingTime: Long = 0,
    val topPerformingSources: List<String> = emptyList(),
    val failedSources: List<String> = emptyList(),
)
