package io.content.scraper.service

import io.content.scraper.api.dto.ArticleDto
import io.content.scraper.api.dto.dashboard.ArticleTrendDto
import io.content.scraper.api.dto.dashboard.DashboardStatsDto
import io.content.scraper.api.dto.dashboard.RecentActivityDto
import io.content.scraper.api.dto.dashboard.SourceHealthDto
import io.content.scraper.repository.ActivityLogRepository
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.repository.SourceRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Service for providing dashboard data
 */
@Service
class DashboardService(
    private val articleRepository: ArticleRepository,
    private val sourceRepository: SourceRepository,
    private val activityLogRepository: ActivityLogRepository,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Get dashboard overview statistics
     */
    fun getDashboardStats(): DashboardStatsDto {
        logger.debug { "Retrieving dashboard statistics" }

        val totalArticles = articleRepository.count()
        val totalSources = sourceRepository.count()
        val activeSources = sourceRepository.countByIsActiveTrue()

        val yesterday = LocalDateTime.now().minus(1, ChronoUnit.DAYS)
        val articlesLast24Hours = articleRepository.countByCreatedAtAfter(yesterday)

        val lastScrapeActivity =
            activityLogRepository
                .findTopByActionOrderByTimestampDesc("SCRAPE")
                ?.timestamp

        return DashboardStatsDto(
            totalArticles = totalArticles,
            totalSources = totalSources,
            totalActiveSources = activeSources,
            articlesLast24Hours = articlesLast24Hours,
            lastScrapeTime = lastScrapeActivity,
        )
    }

    /**
     * Get article trend by scraped date (creation date in the system)
     */
    fun getArticleTrendByScrapedDate(timeRange: String): List<ArticleTrendDto> {
        logger.debug { "Retrieving article trend by scraped date for time range: $timeRange" }

        val (startDate, endDate) = parseTimeRange(timeRange)

        val trendData = articleRepository.countByCreatedAtDateBetween(startDate, endDate)

        return trendData.map { result ->
            ArticleTrendDto(
                date = result.date,
                count = result.count,
            )
        }
    }

    /**
     * Get article trend by published date
     */
    fun getArticleTrendByPublishedDate(timeRange: String): List<ArticleTrendDto> {
        logger.debug { "Retrieving article trend by published date for time range: $timeRange" }

        val (startDate, endDate) = parseTimeRange(timeRange)

        val trendData = articleRepository.countByPublishDateBetween(startDate, endDate)

        return trendData.map { result ->
            ArticleTrendDto(
                date = result.date,
                count = result.count,
            )
        }
    }

    /**
     * Get source health data
     */
    fun getSourceHealth(): List<SourceHealthDto> {
        logger.debug { "Retrieving source health data" }

        val sources = sourceRepository.findAll().toList()

        return sources.map { source ->
            val articlesCount = articleRepository.countBySourceId(source.id)

            SourceHealthDto(
                id = source.id.toString(),
                name = source.name,
                url = source.url,
                lastScraped = source.lastScraped,
                articlesCount = articlesCount,
                isActive = source.isActive,
            )
        }
    }

    /**
     * Get recent scraper activities
     */
    fun getRecentActivities(limit: Int): List<RecentActivityDto> {
        logger.debug { "Retrieving recent activities, limit: $limit" }

        val pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"))
        val activities = activityLogRepository.findAll(pageRequest)

        return activities
            .map { activity ->
                RecentActivityDto(
                    id = activity.id.toString(),
                    timestamp = activity.timestamp,
                    action = activity.action,
                    status = activity.status,
                    details = activity.details ?: "N/A",
                )
            }.toList()
    }

    /**
     * Get recent articles
     */
    fun getRecentArticles(limit: Int): List<ArticleDto> {
        logger.debug { "Retrieving recent articles, limit: $limit" }

        val pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "publishDate"))
        val articles = articleRepository.findAll(pageRequest)

        return articles
            .map { article ->
                ArticleDto.fromEntity(article)
            }.toList()
    }

    /**
     * Parse time range string to date range
     * Format options: 7D, 1M, 3M, 6M, 1Y, ALL
     */
    private fun parseTimeRange(timeRange: String): Pair<LocalDate, LocalDate> {
        val endDate = LocalDate.now()

        val startDate =
            when (timeRange.uppercase()) {
                "7D" -> endDate.minusDays(7)
                "1M" -> endDate.minusMonths(1)
                "3M" -> endDate.minusMonths(3)
                "6M" -> endDate.minusMonths(6)
                "1Y" -> endDate.minusYears(1)
                "ALL" -> endDate.minusYears(10) // Arbitrary past date
                else -> endDate.minusDays(7) // Default to 7 days
            }

        return Pair(startDate, endDate)
    }
}
