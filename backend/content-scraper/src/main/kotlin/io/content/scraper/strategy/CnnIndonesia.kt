package io.content.scraper.strategy

import io.content.scraper.models.ProcessingResult
import org.jsoup.nodes.Document

class CnnIndonesia : NewsParsingStrategy {
    private val contentSelectors =
        listOf(
            ".detail-wrap p",
            ".content-artikel p",
            ".content p",
            "article p",
        )

    override fun parse(document: Document): ProcessingResult<Map<String, String>, String> {
        try {
            val author = getAuthor(document)
            val content = getArticle(document)
            return ProcessingResult.success(mapOf("author" to author, "content" to content))
        } catch (e: Exception) {
            return ProcessingResult.failure(e.message ?: "Failed to extract content")
        }
    }

    private fun getAuthor(document: Document): String {
        document.select("meta[name=author]").firstOrNull()?.let {
            val content = it.attr("content")
            if (content.isNotBlank()) {
                return content.trim()
            }
        }

        return ""
    }

    private fun getArticle(document: Document): String {
        val contentDiv = document.select(".detail-text").first()

        if (contentDiv != null) {
            val paragraphs = contentDiv.select("p")

            val filteredParagraphs =
                paragraphs.filter { paragraph ->
                    !paragraph.hasClass("para_caption")
                }

            return filteredParagraphs.joinToString("\n\n") { it.text().trim() }
        }

        for (selector in contentSelectors) {
            val elements = document.select(selector).filter { element -> element.text().isNotBlank() }
            if (elements.isNotEmpty()) {
                return elements.joinToString("\n\n") { it.text().trim() }
            }
        }

        return document.select(".content, main, article").text().trim()
    }
}
