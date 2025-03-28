package io.content.scraper.strategy.base

import io.content.scraper.models.ProcessingResult
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.nodes.Document

/**
 * Base strategy implementation with template method pattern
 */
abstract class BaseNewsParsingStrategy : NewsParsingStrategy {
    private val logger = KotlinLogging.logger {}

    override fun parse(document: Document): ProcessingResult<Map<String, String>, String> =
        try {
            // Pre-processing: clean document
            cleanDocument(document)

            // Extract data
            val author = getAuthor(document)
            val content = getArticle(document)

            ProcessingResult.success(mapOf("author" to author, "content" to content))
        } catch (e: Exception) {
            logger.error(e) { "Parsing error: ${e.message}" }
            ProcessingResult.failure(e.message ?: "Failed to extract content")
        }

    /**
     * Clean the document before extraction
     */
    protected open fun cleanDocument(document: Document) {
        // Default implementation: remove common unwanted elements
        val commonElementsToRemove =
            listOf(
                // Ads
                "div[id*=div-gpt-ad]",
                "ins.adsbygoogle",
                "script",
                // Related content
                ".baca-juga",
                "div.baca-juga",
                // Social
                ".share",
                ".social",
            )

        commonElementsToRemove.forEach { selector ->
            document.select(selector).remove()
        }
    }

    /**
     * Extract author from the document
     */
    protected abstract fun getAuthor(document: Document): String

    /**
     * Extract article content from the document
     */
    protected abstract fun getArticle(document: Document): String
}
