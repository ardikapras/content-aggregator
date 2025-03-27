package io.content.scraper.api.exception

import io.content.scraper.api.dto.ApiResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Custom exception for news scraper operations
 */
open class NewsScraperException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

/**
 * Exception for content extraction failures
 */
open class ContentExtractionException : NewsScraperException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

/**
 * Exception for source configuration issues
 */
class SourceConfigurationException : NewsScraperException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

/**
 * Custom exception for rate limiting to allow specific retry policies
 */
class RateLimitException(
    message: String,
) : ContentExtractionException(message)

/**
 * Global exception handler
 */
@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(NewsScraperException::class)
    fun handleNewsScraperException(ex: NewsScraperException): ResponseEntity<ApiResponse<Nothing>> {
        logger.error(ex) { "News scraper exception: ${ex.message}" }

        return ApiResponse.error(ex.message ?: "Unknown error")
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        logger.error(ex) { "Unhandled exception: ${ex.message}" }

        return ApiResponse.error("An unexpected error occurred")
    }
}
