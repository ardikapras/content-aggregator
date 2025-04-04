package io.content.scraper.parser.engine

import io.content.scraper.models.ParserConfig
import io.content.scraper.models.ProcessingResult
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * The core parser engine that extracts content based on parser configurations
 */
class ParserEngine(
    private val config: ParserConfig,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Parse a document using the configuration
     */
    fun parse(document: Document): ProcessingResult<Map<String, String>, String> =
        try {
            val author = extractAuthor(document)
            val initialContent = extractContent(document)
            var content = initialContent

            if (!config.nextPageSelector.isNullOrBlank()) {
                content = extractMultiPageContent(document, initialContent)
            }

            ProcessingResult.success(mapOf("author" to author, "content" to content))
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
                    continue
                }

                val element = document.select(selector).firstOrNull() ?: continue

                val authorText =
                    when (element.tagName()) {
                        "meta" -> element.attr("content")
                        else -> element.text()
                    }

                if (authorText.isNotBlank()) {
                    return authorText.trim()
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
    private fun extractContent(document: Document): String {
        cleanDocument(document)
        return config.contentSelectors
            .asSequence()
            .mapNotNull { selector ->
                runCatching {
                    val elements = document.select(selector)
                    if (elements.isNotEmpty()) {
                        elements
                            .map {
                                it.outerHtml()
                            }.filter { it.isNotBlank() }
                            .takeIf { it.isNotEmpty() }
                            ?.joinToString("\n\n")
                    } else {
                        null
                    }
                }.getOrNull()
            }.firstOrNull() ?: ""
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
        val baseUrl = document.baseUri()
        val visitedUrls = mutableSetOf<String>()

        // Add the initial page to visited URLs
        visitedUrls.add(baseUrl)

        repeat(9) {
            // Max pages is 10, but we already have the first page
            try {
                // Get all pagination links
                val nextPageElements = currentDoc.select(config.nextPageSelector!!)

                // Find the next page link that we haven't visited yet
                val nextPageElement =
                    nextPageElements
                        .firstOrNull { element ->
                            val url = getAbsoluteUrl(element, currentDoc.baseUri())
                            !visitedUrls.contains(url) && url.contains("page=")
                        } ?: return content.toString()

                val nextPageUrl = getAbsoluteUrl(nextPageElement, currentDoc.baseUri())
                if (nextPageUrl.isBlank() || visitedUrls.contains(nextPageUrl)) {
                    return content.toString()
                }

                // Mark this URL as visited
                visitedUrls.add(nextPageUrl)

                logger.debug { "Fetching next page: $nextPageUrl" }
                val nextPageDoc = Jsoup.connect(nextPageUrl).get()
                val nextPageContent = extractContent(nextPageDoc)

                if (nextPageContent.isBlank()) {
                    return content.toString()
                }

                content.append("\n\n").append(nextPageContent)
                currentDoc = nextPageDoc
            } catch (e: Exception) {
                logger.error { "Error fetching next page: ${e.message}" }
                return content.toString()
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
            return when {
                href.startsWith("http") -> href
                href.startsWith("/") -> {
                    val protocol = baseUri.substringBefore("://")
                    val domain = baseUri.substringAfter("://").substringBefore("/")
                    "$protocol://$domain$href"
                }
                else -> {
                    val base = if (baseUri.endsWith("/")) baseUri else "$baseUri/"
                    "$base$href"
                }
            }
        }

        return element.select("a").firstOrNull()?.let { getAbsoluteUrl(it, baseUri) } ?: ""
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
     * Clean document before extraction by applying content filters
     */
    private fun cleanDocument(document: Document) {
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
        ).forEach { selector ->
            document.select(selector).remove()
        }

        config.contentFilters.forEach { filter ->
            val elements = document.select(filter)
            when {
                elements.isNotEmpty() -> elements.remove()
                else ->
                    document
                        .select("p, div, span")
                        .filter { element -> element.text().contains(filter) }
                        .forEach { it.remove() }
            }
        }
    }
}
