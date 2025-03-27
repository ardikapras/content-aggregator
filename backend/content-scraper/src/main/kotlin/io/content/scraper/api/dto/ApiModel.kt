package io.content.scraper.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.content.scraper.models.Article
import io.content.scraper.models.Source
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

/**
 * Standard API response wrapper with improved type safety and consistency
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val data: T? = null,
    val message: String? = null,
    val error: String? = null,
    val errors: List<ApiError>? = null,
    val status: Int = if (success) HttpStatus.OK.value() else HttpStatus.BAD_REQUEST.value(),
    val path: String? = null,
) {
    companion object {
        /**
         * Create a successful response
         */
        fun <T> success(
            data: T,
            message: String? = null,
            httpStatus: HttpStatus = HttpStatus.OK,
        ): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.status(httpStatus).body(
                ApiResponse(
                    success = true,
                    data = data,
                    message = message,
                    status = httpStatus.value(),
                ),
            )

        /**
         * Create an error response
         */
        fun <T> error(
            message: String,
            httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
            errors: List<ApiError>? = null,
        ): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.status(httpStatus).body(
                ApiResponse(
                    success = false,
                    message = message,
                    error = message,
                    errors = errors,
                    status = httpStatus.value(),
                ),
            )

        /**
         * Create a not found response
         */
        fun <T> notFound(message: String = "Resource not found"): ResponseEntity<ApiResponse<T>> = error(message, HttpStatus.NOT_FOUND)

        /**
         * Create a validation error response
         */
        fun <T> validationError(
            message: String = "Validation failed",
            errors: List<ApiError>,
        ): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = message,
                    error = message,
                    errors = errors,
                    status = HttpStatus.BAD_REQUEST.value(),
                ),
            )

        /**
         * Create a server error response
         */
        fun <T> serverError(message: String = "Internal server error"): ResponseEntity<ApiResponse<T>> =
            error(message, HttpStatus.INTERNAL_SERVER_ERROR)

        /**
         * Create a no content response
         */
        fun <T> noContent(): ResponseEntity<ApiResponse<T>> = ResponseEntity.noContent().build()
    }
}

/**
 * Detailed API error for validation errors
 */
data class ApiError(
    val field: String,
    val message: String,
    val value: Any? = null,
)

/**
 * Data Transfer Object for NewsSource
 */
data class SourceDto(
    val id: String,
    val name: String,
    val url: String,
    val lastScraped: String?,
    val isActive: Boolean,
) {
    companion object {
        fun fromEntity(source: Source): SourceDto =
            SourceDto(
                id = source.id.toString(),
                name = source.name,
                url = source.url,
                lastScraped = source.lastScraped?.toString() ?: "Never",
                isActive = source.isActive,
            )
    }
}

/**
 * Data Transfer Object for Article
 */
data class ArticleDto(
    val id: String,
    val title: String,
    val description: String,
    val url: String,
    val source: String,
    val publishDate: String?,
    val author: String?,
    val content: String?,
    val wordCount: Int?,
    val readingTimeMinutes: Int?,
    val sentiment: Float?,
    val sentimentLabel: String?,
) {
    companion object {
        fun fromEntity(article: Article): ArticleDto =
            ArticleDto(
                id = article.id.toString(),
                title = article.title,
                description = article.description,
                url = article.url,
                source = article.source.name,
                publishDate = article.publishDate?.toString(),
                author = article.author ?: "Unknown",
                content = article.content,
                wordCount = article.wordCount,
                readingTimeMinutes = article.readingTimeMinutes,
                sentiment = article.sentiment,
                sentimentLabel = getSentimentLabel(article.sentiment),
            )

        private fun getSentimentLabel(sentiment: Float?): String? {
            if (sentiment == null) return null

            return when {
                sentiment > 0.3f -> "POSITIVE"
                sentiment < -0.3f -> "NEGATIVE"
                else -> "NEUTRAL"
            }
        }
    }
}

/**
 * Data Transfer Object for Category
 */
data class CategoryDto(
    val id: String,
    val name: String,
    val description: String?,
)
