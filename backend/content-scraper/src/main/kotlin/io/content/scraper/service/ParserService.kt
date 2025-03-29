package io.content.scraper.service

import io.content.scraper.api.exception.NewsScraperException
import io.content.scraper.enums.ActivityLogAction
import io.content.scraper.enums.ActivityLogStatus
import io.content.scraper.models.ParserConfig
import io.content.scraper.models.ProcessingResult
import io.content.scraper.parser.ParserManager
import io.content.scraper.repository.ParserConfigRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Service for managing parser configurations
 */
@Service
class ParserService(
    private val parserConfigRepository: ParserConfigRepository,
    private val activityLogService: ActivityLogService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Initialize the parser manager on startup
     */
    @PostConstruct
    fun initialize() {
        logger.info { "Initializing Parser Configuration Service" }
        val configs = parserConfigRepository.findAll()
        ParserManager.initialize(configs)
        logger.info { "Loaded ${configs.size} parser configurations" }
    }

    /**
     * Get all parser configurations
     */
    fun getAllParserConfigs(): List<ParserConfig> = parserConfigRepository.findAll()

    /**
     * Get a parser configuration by ID
     */
    fun getParserConfigById(id: UUID): ParserConfig =
        parserConfigRepository.findById(id).orElseThrow {
            NewsScraperException("Parser configuration not found with ID: $id")
        }

    /**
     * Get a parser configuration by name
     */
    fun getParserConfigByName(name: String): ParserConfig =
        parserConfigRepository.findByName(name) ?: throw NewsScraperException(
            "Parser configuration not found with name: $name",
        )

    /**
     * Create a new parser configuration
     */
    @Transactional
    fun createParserConfig(config: ParserConfig): ParserConfig {
        if (parserConfigRepository.findByName(config.name) != null) {
            activityLogService.logActivity(
                ActivityLogAction.PARSER_CONFIG.name,
                ActivityLogStatus.FAILED.name,
                "Another parser configuration with name '${config.name}' already exists",
            )
            throw NewsScraperException("A parser configuration with name '${config.name}' already exists")
        }

        val saved = parserConfigRepository.save(config)
        ParserManager.updateConfig(saved)
        activityLogService.logActivity(
            ActivityLogAction.PARSER_CONFIG.name,
            ActivityLogStatus.SUCCESS.name,
            "Created parser configuration with name: ${saved.name}",
        )
        return saved
    }

    /**
     * Update an existing parser configuration
     */
    @Transactional
    fun updateParserConfig(
        id: UUID,
        config: ParserConfig,
    ): ParserConfig {
        if (!parserConfigRepository.existsById(id)) {
            activityLogService.logActivity(
                ActivityLogAction.PARSER_CONFIG.name,
                ActivityLogStatus.FAILED.name,
                "Parser configuration not found with ID: $id",
            )
            throw NewsScraperException("Parser configuration not found with ID: $id")
        }

        val existing = parserConfigRepository.findByName(config.name)
        if (existing != null && existing.id != id) {
            activityLogService.logActivity(
                ActivityLogAction.PARSER_CONFIG.name,
                ActivityLogStatus.FAILED.name,
                "Another parser configuration with name '${config.name}' already exists",
            )
            throw NewsScraperException("Another parser configuration with name '${config.name}' already exists")
        }

        val toUpdate = config.copy(id = id)
        val saved = parserConfigRepository.save(toUpdate)
        ParserManager.updateConfig(saved)
        activityLogService.logActivity(
            ActivityLogAction.PARSER_CONFIG.name,
            ActivityLogStatus.SUCCESS.name,
            "Updated parser configuration with name: ${saved.name}",
        )
        return saved
    }

    /**
     * Delete a parser configuration by ID
     */
    @Transactional
    fun deleteParserConfig(id: UUID) {
        if (!parserConfigRepository.existsById(id)) {
            activityLogService.logActivity(
                ActivityLogAction.PARSER_CONFIG.name,
                ActivityLogStatus.FAILED.name,
                "Parser configuration not found with ID: $id",
            )
            throw NewsScraperException("Parser configuration not found with ID: $id")
        }

        parserConfigRepository.deleteById(id)
        activityLogService.logActivity(
            ActivityLogAction.PARSER_CONFIG.name,
            ActivityLogStatus.SUCCESS.name,
            "Deleted parser configuration with ID: $id",
        )
    }

    /**
     * Test a parser configuration against a URL
     */
    fun testParserConfig(
        url: String,
        config: ParserConfig,
    ): ProcessingResult<Map<String, String>, String> {
        try {
            logger.info { "Testing parser configuration on URL: $url" }
            val document = Jsoup.connect(url).get()

            ParserManager.updateConfig(config)

            return ParserManager.parseWithConfig(document, config.id)
        } catch (e: Exception) {
            logger.error(e) { "Error testing parser configuration on URL: $url" }
            return ProcessingResult.failure("Failed to test configuration: ${e.message}")
        }
    }
}
