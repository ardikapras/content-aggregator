package io.content.scraper.strategy.util

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

object ParserUtils {
    private val logger = KotlinLogging.logger {}

    fun parseAuthorFromLdJson(
        doc: Document,
        default: String,
    ): String {
        doc.select("script[type=application/ld+json]").forEach { scriptTag ->
            try {
                val jsonContent = scriptTag.html()
                val json = Json { ignoreUnknownKeys = true }
                when (val jsonElement = json.parseToJsonElement(jsonContent)) {
                    is JsonObject -> {
                        val authorElement = jsonElement["author"]
                        if (authorElement is JsonObject && authorElement.containsKey("name")) {
                            return authorElement["name"]?.jsonPrimitive?.contentOrNull ?: ""
                        }
                    }

                    else -> return default
                }
            } catch (e: Exception) {
                logger.debug { "Error parsing JSON-LD: ${e.message}" }
            }
        }
        return default
    }

    /**
     * Extract author from meta tags
     */
    fun parseAuthorFromMeta(
        doc: Document,
        metaNames: List<String>,
        default: String,
    ): String {
        for (name in metaNames) {
            doc.select("meta[name=$name]").firstOrNull()?.let {
                val content = it.attr("content")
                if (content.isNotBlank()) {
                    return content.trim()
                }
            }
        }
        return default
    }

    /**
     * Clean document by removing unwanted elements
     */
    fun cleanDocument(
        doc: Document,
        elementsToRemove: Map<String, List<String>>,
    ) {
        elementsToRemove.forEach { (_, selectors) ->
            selectors.forEach { selector ->
                doc.select(selector).remove()
            }
        }
    }

    /**
     * Filter paragraphs by various conditions
     */
    fun filterParagraphs(
        paragraphs: Elements,
        excludePatterns: List<Regex> = emptyList(),
    ): List<Element> =
        paragraphs.filter { paragraph ->
            val text = paragraph.text().trim()
            text.isNotBlank() && !excludePatterns.any { pattern -> pattern.matches(text) }
        }

    /**
     * Extract article content from multiple possible selectors
     */
    fun extractFromSelectors(
        doc: Document,
        selectors: List<String>,
    ): String {
        for (selector in selectors) {
            val elements = doc.select(selector).filter { element -> element.text().isNotBlank() }
            if (elements.isNotEmpty()) {
                return elements.joinToString("\n\n") { it.text().trim() }
            }
        }

        // Fallback to most common content containers
        return doc.select(".content, main, article").text().trim()
    }
}
