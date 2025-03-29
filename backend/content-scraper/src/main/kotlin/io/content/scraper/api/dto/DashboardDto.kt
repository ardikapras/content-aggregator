package io.content.scraper.api.dto.dashboard

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * DTO for dashboard overview statistics
 */
data class DashboardStatsDto(
    val totalArticles: Long,
    val totalSources: Long,
    val totalActiveSources: Long,
    val articlesLast24Hours: Long,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val lastScrapeTime: LocalDateTime?,
)

/**
 * DTO for article trend data
 */
data class ArticleTrendDto(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val date: LocalDate,
    val count: Long,
)

/**
 * DTO for source health status
 */
data class SourceHealthDto(
    val id: String,
    val name: String,
    val url: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val lastScraped: LocalDateTime?,
    val articlesCount: Long,
    val isActive: Boolean,
)

/**
 * DTO for recent scraper activities
 */
data class RecentActivityDto(
    val id: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val timestamp: LocalDateTime,
    val action: String,
    val status: String,
    val details: String,
)

/**
 * Interface for date and count result in JPA queries
 */
interface DateCountResult {
    val date: LocalDate
    val count: Long
}
