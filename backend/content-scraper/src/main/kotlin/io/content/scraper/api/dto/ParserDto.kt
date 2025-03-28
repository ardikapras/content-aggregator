package io.content.scraper.api.dto

import io.content.scraper.models.ParserConfig
import java.time.LocalDateTime

/**
 * Data Transfer Object for ParserConfig
 */
data class ParserConfigDto(
    val id: String,
    val name: String,
    val description: String?,
    val authorSelectors: List<String>,
    val contentSelectors: List<String>,
    val nextPageSelector: String?,
    val contentFilters: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun fromEntity(entity: ParserConfig): ParserConfigDto =
            ParserConfigDto(
                id = entity.id.toString(),
                name = entity.name,
                description = entity.description,
                authorSelectors = entity.authorSelectors,
                contentSelectors = entity.contentSelectors,
                nextPageSelector = entity.nextPageSelector,
                contentFilters = entity.contentFilters,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            )
    }
}

/**
 * Request for creating a new parser configuration
 */
data class CreateParserConfigRequest(
    val name: String,
    val description: String?,
    val authorSelectors: List<String>,
    val contentSelectors: List<String>,
    val nextPageSelector: String?,
    val contentFilters: List<String>,
) {
    fun toEntity(): ParserConfig =
        ParserConfig(
            name = name,
            description = description,
            authorSelectors = authorSelectors,
            contentSelectors = contentSelectors,
            nextPageSelector = nextPageSelector,
            contentFilters = contentFilters,
        )
}

/**
 * Request for updating an existing parser configuration
 */
data class UpdateParserConfigRequest(
    val name: String?,
    val description: String?,
    val authorSelectors: List<String>?,
    val contentSelectors: List<String>?,
    val nextPageSelector: String?,
    val contentFilters: List<String>?,
) {
    fun toEntity(existing: ParserConfig): ParserConfig =
        ParserConfig(
            id = existing.id,
            name = name ?: existing.name,
            description = description ?: existing.description,
            authorSelectors = authorSelectors ?: existing.authorSelectors,
            contentSelectors = contentSelectors ?: existing.contentSelectors,
            nextPageSelector = nextPageSelector,
            contentFilters = contentFilters ?: existing.contentFilters,
        )
}

/**
 * Request for testing a parser configuration
 */
data class ParserTestRequest(
    val url: String,
    val config: CreateParserConfigRequest,
)

/**
 * Response for parser test results
 */
data class ParserTestResponse(
    val author: String?,
    val contentPreview: String?,
    val success: Boolean,
    val message: String?,
)
