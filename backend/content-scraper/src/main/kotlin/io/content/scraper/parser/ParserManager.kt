package io.content.scraper.parser

import io.content.scraper.models.ParserConfig
import io.content.scraper.models.ProcessingResult
import io.content.scraper.parser.engine.ParserEngine
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.nodes.Document
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manager for parser configurations and engine creation
 */
object ParserManager {
    private val logger = KotlinLogging.logger {}
    private val configCacheById = ConcurrentHashMap<UUID, ParserConfig>()
    private val configCacheByName = ConcurrentHashMap<String, ParserConfig>()
    private lateinit var defaultConfig: ParserConfig

    /**
     * Initialize the manager with a list of parser configurations
     */
    fun initialize(configs: List<ParserConfig>) {
        logger.info { "Initializing ParserManager with ${configs.size} parser configurations" }

        configCacheById.clear()
        configCacheByName.clear()

        configs.forEach { config ->
            configCacheById[config.id] = config
            configCacheByName[config.name] = config

            if (config.name == "DEFAULT") {
                defaultConfig = config
            }
        }

        if (!::defaultConfig.isInitialized && configs.isNotEmpty()) {
            defaultConfig = configs.first()
            logger.warn { "No DEFAULT parser config found, using ${defaultConfig.name} as default" }
        }

        logger.info { "ParserManager initialized successfully" }
    }

    /**
     * Add or update a parser configuration in the cache
     */
    fun updateConfig(config: ParserConfig) {
        configCacheById[config.id] = config
        configCacheByName[config.name] = config
    }

    /**
     * Get a parser engine by config name
     */
    fun getParserByName(name: String): ParserEngine {
        val config =
            configCacheByName[name] ?: run {
                logger.warn { "Parser configuration not found for name: $name, using default" }
                if (::defaultConfig.isInitialized) defaultConfig else createDefaultConfig()
            }
        return ParserEngine(config)
    }

    /**
     * Get a parser engine by config ID
     */
    fun getParserById(id: UUID): ParserEngine {
        val config =
            configCacheById[id] ?: run {
                logger.warn { "Parser configuration not found for ID: $id, using default" }
                if (::defaultConfig.isInitialized) defaultConfig else createDefaultConfig()
            }
        return ParserEngine(config)
    }

    /**
     * Parse a document using the specified parser configuration
     */
    fun parseWithConfig(
        document: Document,
        configId: UUID,
    ): ProcessingResult<Map<String, String>, String> {
        val parser = getParserById(configId)
        return parser.parse(document)
    }

    /**
     * Parse a document using the specified parser configuration name
     */
    fun parseWithConfig(
        document: Document,
        configName: String,
    ): ProcessingResult<Map<String, String>, String> {
        val parser = getParserByName(configName)
        return parser.parse(document)
    }

    /**
     * Create a default configuration if none is available
     */
    private fun createDefaultConfig(): ParserConfig {
        logger.warn { "Creating temporary default parser configuration" }
        return ParserConfig(
            name = "DEFAULT",
            description = "Temporary default configuration",
            authorSelectors = listOf("meta[name=author]", "script[type=application/ld+json]"),
            contentSelectors = listOf("article p", "main p", ".content p"),
        ).also {
            defaultConfig = it
            configCacheById[it.id] = it
            configCacheByName[it.name] = it
        }
    }
}
