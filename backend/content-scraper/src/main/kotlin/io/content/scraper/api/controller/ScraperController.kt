package io.content.scraper.api.controller

import io.content.scraper.api.dto.ApiResponse
import io.content.scraper.repository.SourceRepository
import io.content.scraper.service.CollectorService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for handling scraper-related operations
 * - Managing news sources
 * - Triggering scraping operations
 * - Retrieving articles and categories
 */
@RestController
@RequestMapping("/api/scraper")
class ScraperController(
    private val sourceRepository: SourceRepository,
    private val collectorService: CollectorService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Trigger scraping for all active news sources
     */
    @PostMapping("/run")
    fun triggerScraping(): ResponseEntity<ApiResponse<String>> =
        runBlocking {
            try {
                collectorService.start()
                return@runBlocking ApiResponse.success(
                    "Scraping completed successfully",
                )
            } catch (e: Exception) {
                logger.error { "Error triggering scraping: ${e.message}" }

                return@runBlocking ApiResponse.error("Failed to complete scraping: ${e.message}")
            }
        }
}
