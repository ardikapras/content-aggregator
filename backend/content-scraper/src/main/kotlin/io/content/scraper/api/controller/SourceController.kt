package io.content.scraper.api.controller

import io.content.scraper.api.dto.ApiResponse
import io.content.scraper.api.dto.SourceDto
import io.content.scraper.models.Source
import io.content.scraper.repository.SourceRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/sources")
class SourceController(
    private val sourceRepository: SourceRepository,
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping
    fun getAllSources(): ResponseEntity<ApiResponse<List<SourceDto>>> {
        try {
            val sources =
                sourceRepository
                    .findAll()
                    .map { SourceDto.fromEntity(it) }

            return ApiResponse.success(
                sources,
                "Successfully retrieved all sources",
            )
        } catch (e: Exception) {
            logger.error(e) { "Error fetching sources: ${e.message}" }
            return ApiResponse.error("Failed to retrieve sources: ${e.message}")
        }
    }

    @GetMapping("/{id}")
    fun getSourceById(
        @PathVariable id: String,
    ): ResponseEntity<ApiResponse<SourceDto>> {
        try {
            val uuid =
                try {
                    UUID.fromString(id)
                } catch (e: IllegalArgumentException) {
                    return ApiResponse.error("Invalid source ID format")
                }

            return sourceRepository
                .findById(uuid)
                .map { source -> ApiResponse.success(SourceDto.fromEntity(source)) }
                .orElse(ApiResponse.notFound("Source not found with ID: $id"))
        } catch (e: Exception) {
            logger.error(e) { "Error fetching source $id: ${e.message}" }
            return ApiResponse.error("Failed to retrieve source: ${e.message}")
        }
    }

    @GetMapping("/active")
    fun getActiveSources(): ResponseEntity<ApiResponse<List<SourceDto>>> {
        try {
            val sources =
                sourceRepository
                    .findByIsActiveTrue()
                    .map { SourceDto.fromEntity(it) }

            return ApiResponse.success(
                sources,
                "Successfully retrieved active sources",
            )
        } catch (e: Exception) {
            logger.error(e) { "Error fetching active sources: ${e.message}" }
            return ApiResponse.error("Failed to retrieve active sources: ${e.message}")
        }
    }

    @PostMapping
    fun createSource(
        @RequestBody request: CreateSourceRequest,
    ): ResponseEntity<ApiResponse<SourceDto>> {
        try {
            if (request.name.isBlank() || request.url.isBlank()) {
                return ApiResponse.error("Name and URL are required")
            }

            val source =
                Source(
                    name = request.name,
                    url = request.url,
                    parsingStrategy = request.parsingStrategy,
                    isActive = request.isActive,
                )

            val savedSource = sourceRepository.save(source)
            return ApiResponse.success(
                SourceDto.fromEntity(savedSource),
                "Source created successfully",
            )
        } catch (e: Exception) {
            logger.error(e) { "Error creating source: ${e.message}" }
            return ApiResponse.error("Failed to create source: ${e.message}")
        }
    }

    @PutMapping("/{id}")
    fun updateSource(
        @PathVariable id: String,
        @RequestBody request: UpdateSourceRequest,
    ): ResponseEntity<ApiResponse<SourceDto>> {
        try {
            val uuid =
                try {
                    UUID.fromString(id)
                } catch (e: IllegalArgumentException) {
                    return ApiResponse.error("Invalid source ID format")
                }

            val existingSource =
                sourceRepository
                    .findById(uuid)
                    .orElse(null) ?: return ApiResponse.notFound("Source not found with ID: $id")

            val updatedSource =
                existingSource.copy(
                    name = request.name ?: existingSource.name,
                    url = request.url ?: existingSource.url,
                    parsingStrategy = request.parsingStrategy ?: existingSource.parsingStrategy,
                    isActive = request.isActive ?: existingSource.isActive,
                )

            val savedSource = sourceRepository.save(updatedSource)
            return ApiResponse.success(
                SourceDto.fromEntity(savedSource),
                "Source updated successfully",
            )
        } catch (e: Exception) {
            logger.error(e) { "Error updating source $id: ${e.message}" }
            return ApiResponse.error("Failed to update source: ${e.message}")
        }
    }

    @PutMapping("/{id}/toggle-active")
    fun toggleSourceActive(
        @PathVariable id: String,
    ): ResponseEntity<ApiResponse<SourceDto>> {
        try {
            val uuid =
                try {
                    UUID.fromString(id)
                } catch (e: IllegalArgumentException) {
                    return ApiResponse.error("Invalid source ID format")
                }

            val existingSource =
                sourceRepository
                    .findById(uuid)
                    .orElse(null) ?: return ApiResponse.notFound("Source not found with ID: $id")

            val updatedSource =
                existingSource.copy(
                    isActive = !existingSource.isActive,
                )

            val savedSource = sourceRepository.save(updatedSource)
            return ApiResponse.success(
                SourceDto.fromEntity(savedSource),
                "Source ${if (savedSource.isActive) "activated" else "deactivated"} successfully",
            )
        } catch (e: Exception) {
            logger.error(e) { "Error toggling source $id: ${e.message}" }
            return ApiResponse.error("Failed to toggle source: ${e.message}")
        }
    }
}

data class CreateSourceRequest(
    val name: String,
    val url: String,
    val parsingStrategy: String = "DEFAULT",
    val isActive: Boolean = true,
)

data class UpdateSourceRequest(
    val name: String? = null,
    val url: String? = null,
    val parsingStrategy: String? = null,
    val isActive: Boolean? = null,
)
