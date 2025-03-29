package io.content.scraper.api.controller

import io.content.scraper.api.dto.ApiResponse
import io.content.scraper.api.dto.CreateParserConfigRequest
import io.content.scraper.api.dto.ParserConfigDto
import io.content.scraper.api.dto.ParserTestRequest
import io.content.scraper.api.dto.ParserTestResponse
import io.content.scraper.api.dto.UpdateParserConfigRequest
import io.content.scraper.service.ParserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * Controller for managing parser configurations
 */
@RestController
@RequestMapping("/api/parser-configs")
class ParserController(
    private val parserService: ParserService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Get all parser configurations
     */
    @GetMapping
    fun getAllParserConfigs(): ResponseEntity<ApiResponse<List<ParserConfigDto>>> {
        try {
            val configs =
                parserService
                    .getAllParserConfigs()
                    .map { ParserConfigDto.fromEntity(it) }
            return ApiResponse.success(configs, "Successfully retrieved parser configurations")
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving parser configurations: ${e.message}" }
            return ApiResponse.error("Failed to retrieve parser configurations: ${e.message}")
        }
    }

    /**
     * Get a parser configuration by ID
     */
    @GetMapping("/{id}")
    fun getParserConfigById(
        @PathVariable id: String,
    ): ResponseEntity<ApiResponse<ParserConfigDto>> {
        try {
            val configId = UUID.fromString(id)
            val config = parserService.getParserConfigById(configId)
            return ApiResponse.success(
                ParserConfigDto.fromEntity(config),
                "Successfully retrieved parser configuration",
            )
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving parser configuration: ${e.message}" }
            return ApiResponse.error("Failed to retrieve parser configuration: ${e.message}")
        }
    }

    /**
     * Create a new parser configuration
     */
    @PostMapping
    fun createParserConfig(
        @RequestBody request: CreateParserConfigRequest,
    ): ResponseEntity<ApiResponse<ParserConfigDto>> {
        try {
            val config = request.toEntity()
            val createdConfig = parserService.createParserConfig(config)
            return ApiResponse.success(
                ParserConfigDto.fromEntity(createdConfig),
                "Parser configuration created successfully",
            )
        } catch (e: Exception) {
            logger.error(e) { "Error creating parser configuration: ${e.message}" }
            return ApiResponse.error("Failed to create parser configuration: ${e.message}")
        }
    }

    /**
     * Update an existing parser configuration
     */
    @PutMapping("/{id}")
    fun updateParserConfig(
        @PathVariable id: String,
        @RequestBody request: UpdateParserConfigRequest,
    ): ResponseEntity<ApiResponse<ParserConfigDto>> {
        try {
            val configId = UUID.fromString(id)
            val existingConfig = parserService.getParserConfigById(configId)
            val configToUpdate = request.toEntity(existingConfig)

            val updatedConfig = parserService.updateParserConfig(configId, configToUpdate)
            return ApiResponse.success(
                ParserConfigDto.fromEntity(updatedConfig),
                "Parser configuration updated successfully",
            )
        } catch (e: Exception) {
            logger.error(e) { "Error updating parser configuration: ${e.message}" }
            return ApiResponse.error("Failed to update parser configuration: ${e.message}")
        }
    }

    /**
     * Delete a parser configuration
     */
    @DeleteMapping("/{id}")
    fun deleteParserConfig(
        @PathVariable id: String,
    ): ResponseEntity<ApiResponse<Any?>> {
        try {
            val configId = UUID.fromString(id)
            parserService.deleteParserConfig(configId)
            return ApiResponse.success(data = null, message = "Parser configuration deleted successfully")
        } catch (e: Exception) {
            logger.error(e) { "Error deleting parser configuration: ${e.message}" }
            return ApiResponse.error("Failed to delete parser configuration: ${e.message}")
        }
    }

    /**
     * Test a parser configuration on a URL
     */
    @PostMapping("/test")
    fun testParserConfig(
        @RequestBody testRequest: ParserTestRequest,
        @RequestParam(defaultValue = "false") fullContent: Boolean = false,
    ): ResponseEntity<ApiResponse<ParserTestResponse>> {
        try {
            val config = testRequest.config.toEntity()
            val result = parserService.testParserConfig(testRequest.url, config)

            return if (result.success) {
                val content = result.successValue["content"] ?: ""
                val contentPreview =
                    if (fullContent) {
                        content
                    } else {
                        content.substring(0, 500) + "..."
                    }

                val response =
                    ParserTestResponse(
                        author = result.successValue["author"],
                        contentPreview = contentPreview,
                        success = true,
                        message = "Parser test completed successfully",
                    )

                ApiResponse.success(response, "Parser test completed successfully")
            } else {
                val response =
                    ParserTestResponse(
                        author = null,
                        contentPreview = null,
                        success = false,
                        message = result.failureValue,
                    )

                ApiResponse.success(response, "Parser test failed")
            }
        } catch (e: Exception) {
            logger.error(e) { "Error testing parser configuration: ${e.message}" }
            return ApiResponse.error("Failed to test parser configuration: ${e.message}")
        }
    }
}
