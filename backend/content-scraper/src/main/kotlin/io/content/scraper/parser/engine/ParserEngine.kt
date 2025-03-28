package io.content.scraper.parser.engine

import io.content.scraper.models.ParserConfig
import io.content.scraper.models.ProcessingResult
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.regex.Pattern

/**
 * The core parser engine that extracts content based on parser configurations
 */
class ParserEngine(
    private val config: ParserConfig,
) {
    private val logger = KotlinLogging.logger {}
    private val contentFiltersPatterns = config.contentFilters.map { Pattern.compile(it) }

    /**
     * Parse a document using the configuration
     */
    fun parse(document: Document): ProcessingResult<Map<String, String>, String> =
        try {
            cleanDocument(document)
            val author = extractAuthor(document)
            val initialContent = extractContent(document)
            var content = initialContent

            if (!config.nextPageSelector.isNullOrBlank()) {
                content = extractMultiPageContent(document, initialContent)
            }

            ProcessingResult.success(
                mapOf(
                    "author" to author,
                    "content" to content,
                ),
            )
        } catch (e: Exception) {
            logger.error(e) { "Error parsing document: ${e.message}" }
            ProcessingResult.failure("Failed to parse document: ${e.message}")
        }

    /**
     * Extract author using configured selectors
     */
    private fun extractAuthor(document: Document): String {
        if (config.authorSelectors.isEmpty()) {
            return "Unknown"
        }

        for (selector in config.authorSelectors) {
            try {
                if (selector.contains("script[type=application/ld+json]")) {
                    val author = extractAuthorFromJsonLd(document)
                    if (author.isNotBlank()) {
                        return author
                    }
                } else {
                    val elements = document.select(selector)
                    if (elements.isNotEmpty()) {
                        val authorElement = elements.first() ?: continue
                        if (authorElement.tagName() == "meta") {
                            val content = authorElement.attr("content")
                            if (content.isNotBlank()) {
                                return content.trim()
                            }
                        } else {
                            val text = authorElement.text()
                            if (text.isNotBlank()) {
                                return text.trim()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logger.debug { "Error extracting author with selector $selector: ${e.message}" }
            }
        }

        return "Unknown"
    }

    /**
     * Extract content using configured selectors
     */
    fun extractContent(document: Document): String {
        if (config.contentSelectors.isEmpty()) {
            return ""
        }

        for (selector in config.contentSelectors) {
            try {
                val elements = document.select(selector)
                if (elements.isNotEmpty()) {
                    val paragraphs =
                        elements
                            .map { it.text().trim() }
                            .filter { it.isNotBlank() }
                            .filter { content -> !isFiltered(content) }

                    if (paragraphs.isNotEmpty()) {
                        return paragraphs.joinToString("\n\n")
                    }
                }
            } catch (e: Exception) {
                logger.debug { "Error extracting content with selector $selector: ${e.message}" }
            }
        }

        return ""
    }

    /**
     * Handle multi-page content extraction
     */
    private fun extractMultiPageContent(
        document: Document,
        initialContent: String,
    ): String {
        val content = StringBuilder(initialContent)
        var currentDoc = document
        var pageCount = 1
        val maxPages = 10

        while (pageCount < maxPages) {
            try {
                val nextPageElements = currentDoc.select(config.nextPageSelector!!)
                if (nextPageElements.isEmpty()) {
                    break
                }

                val nextPageElement = nextPageElements.first() ?: continue
                val nextPageUrl = getAbsoluteUrl(nextPageElement, document.baseUri())

                if (nextPageUrl.isBlank()) {
                    break
                }

                val nextPageDoc = Jsoup.connect(nextPageUrl).get()
                val nextPageContent = extractContent(nextPageDoc)

                if (nextPageContent.isBlank()) {
                    break
                }

                content.append("\n\n").append(nextPageContent)
                currentDoc = nextPageDoc
                pageCount++
            } catch (e: Exception) {
                logger.error { "Error fetching next page: ${e.message}" }
                break
            }
        }

        return content.toString()
    }

    /**
     * Get absolute URL from a link element
     */
    private fun getAbsoluteUrl(
        element: Element,
        baseUri: String,
    ): String {
        if (element.tagName() == "a") {
            val href = element.attr("href")
            return if (href.startsWith("http")) {
                href
            } else {
                val base = if (baseUri.endsWith("/")) baseUri else "$baseUri/"
                if (href.startsWith("/")) {
                    val protocol = baseUri.substringBefore("://")
                    val domain = baseUri.substringAfter("://").substringBefore("/")
                    "$protocol://$domain$href"
                } else {
                    "$base$href"
                }
            }
        }

        val anchor = element.select("a").firstOrNull()
        if (anchor != null) {
            return getAbsoluteUrl(anchor, baseUri)
        }

        return ""
    }

    /**
     * Extract author from JSON-LD script
     */
    private fun extractAuthorFromJsonLd(document: Document): String {
        val scripts = document.select("script[type=application/ld+json]")
        for (script in scripts) {
            try {
                val jsonString = script.data()

                if (jsonString.contains("\"author\"")) {
                    val namePattern = "\"author\"\\s*:\\s*\\{[^}]*\"name\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                    val match = namePattern.find(jsonString)
                    if (match != null) {
                        return match.groupValues[1]
                    }
                }
            } catch (e: Exception) {
                logger.debug { "Error parsing JSON-LD: ${e.message}" }
            }
        }
        return ""
    }

    /**
     * Check if content should be filtered out
     */
    private fun isFiltered(content: String): Boolean =
        contentFiltersPatterns.any { pattern ->
            pattern.matcher(content).find()
        }

    /**
     * Clean document before extraction
     */
    private fun cleanDocument(document: Document) {
        val commonElementsToRemove =
            listOf(
                "script",
                "style",
                "iframe",
                "div[id*=div-gpt-ad]",
                "ins.adsbygoogle",
                ".advertisement",
                ".ads",
                ".share-buttons",
                ".social-bar",
                ".comments",
            )

        for (selector in commonElementsToRemove) {
            document.select(selector).remove()
        }
    }
}
