package io.content.scraper.api.controller

import io.content.scraper.api.dto.ApiResponse
import io.content.scraper.api.dto.ArticleDto
import io.content.scraper.api.dto.dashboard.ArticleTrendDto
import io.content.scraper.api.dto.dashboard.DashboardStatsDto
import io.content.scraper.api.dto.dashboard.RecentActivityDto
import io.content.scraper.api.dto.dashboard.SourceHealthDto
import io.content.scraper.service.DashboardService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for providing dashboard-related data
 */
@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val dashboardService: DashboardService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Get overview statistics for dashboard
     */
    @GetMapping("/stats")
    fun getDashboardStats(): ResponseEntity<ApiResponse<DashboardStatsDto>> =
        try {
            val stats = dashboardService.getDashboardStats()
            ApiResponse.success(stats, "Dashboard statistics retrieved successfully")
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving dashboard statistics: ${e.message}" }
            ApiResponse.error("Failed to retrieve dashboard statistics")
        }

    /**
     * Get article trend data by scraped date
     */
    @GetMapping("/trends/scraped")
    fun getArticleTrendByScrapedDate(
        @RequestParam(required = false, defaultValue = "7D") range: String,
    ): ResponseEntity<ApiResponse<List<ArticleTrendDto>>> =
        try {
            val trendData = dashboardService.getArticleTrendByScrapedDate(range)
            ApiResponse.success(trendData, "Article trend data retrieved successfully")
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving article trend data: ${e.message}" }
            ApiResponse.error("Failed to retrieve article trend data")
        }

    /**
     * Get article trend data by published date
     */
    @GetMapping("/trends/published")
    fun getArticleTrendByPublishedDate(
        @RequestParam(required = false, defaultValue = "7D") range: String,
    ): ResponseEntity<ApiResponse<List<ArticleTrendDto>>> =
        try {
            val trendData = dashboardService.getArticleTrendByPublishedDate(range)
            ApiResponse.success(trendData, "Article trend data retrieved successfully")
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving article trend data: ${e.message}" }
            ApiResponse.error("Failed to retrieve article trend data")
        }

    /**
     * Get source health data
     */
    @GetMapping("/sources/health")
    fun getSourceHealth(): ResponseEntity<ApiResponse<List<SourceHealthDto>>> =
        try {
            val sourceHealth = dashboardService.getSourceHealth()
            ApiResponse.success(sourceHealth, "Source health data retrieved successfully")
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving source health data: ${e.message}" }
            ApiResponse.error("Failed to retrieve source health data")
        }

    /**
     * Get recent scraper activities
     */
    @GetMapping("/activities")
    fun getRecentActivities(
        @RequestParam(required = false, defaultValue = "5") limit: Int,
    ): ResponseEntity<ApiResponse<List<RecentActivityDto>>> =
        try {
            val activities = dashboardService.getRecentActivities(limit)
            ApiResponse.success(activities, "Recent activities retrieved successfully")
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving recent activities: ${e.message}" }
            ApiResponse.error("Failed to retrieve recent activities")
        }

    /**
     * Get most recent articles
     */
    @GetMapping("/articles/recent")
    fun getRecentArticles(
        @RequestParam(required = false, defaultValue = "5") limit: Int,
    ): ResponseEntity<ApiResponse<List<ArticleDto>>> =
        try {
            val articles = dashboardService.getRecentArticles(limit)
            ApiResponse.success(articles, "Recent articles retrieved successfully")
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving recent articles: ${e.message}" }
            ApiResponse.error("Failed to retrieve recent articles")
        }
}
