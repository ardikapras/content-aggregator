package io.content.scraper.strategy

import io.content.scraper.models.ProcessingResult
import io.content.scraper.util.ParserUtils.parseAuthorFromLdJson
import org.jsoup.nodes.Document

class CnbcIndonesia : NewsParsingStrategy {
    private val elementsToRemove =
        mapOf(
            "advertisements" to
                listOf(
                    "div[style*=advertisement]",
                    "[id*=div-gpt-ad]",
                    ".banner",
                    "ins.adsbygoogle",
                ),
            "relatedContent" to
                listOf(
                    ".linksisip",
                    ".baca-juga",
                ),
            "socialSharing" to
                listOf(
                    ".detail-share",
                    ".share",
                ),
            "multimedia" to
                listOf(
                    ".video_detail",
                    "iframe",
                ),
            "comments" to
                listOf(
                    "#komentar",
                ),
        )

    override fun parse(document: Document): ProcessingResult<Map<String, String>, String> {
        try {
            elementsToRemove.forEach { (_, selectors) ->
                selectors.forEach { selector ->
                    document.select(selector).remove()
                }
            }
            val author = getAuthor(document)
            val content = getArticle(document)
            return ProcessingResult.success(mapOf("author" to author, "content" to content))
        } catch (e: Exception) {
            return ProcessingResult.failure(e.message ?: "Failed to extract content")
        }
    }

    private fun getAuthor(document: Document): String = parseAuthorFromLdJson(document, "CNBC Indonesia")

    private fun getArticle(document: Document): String {
        val contentDiv = document.select(".detail-text").first()

        if (contentDiv != null) {
            val paragraphs = contentDiv.select("p")

            val filteredParagraphs =
                paragraphs.filter { paragraph ->
                    !paragraph.text().contains(Regex("\\([a-z]{2,3}/[a-z]{2,3}\\)"))
                }

            return filteredParagraphs.joinToString("\n\n") { it.text().trim() }
        }

        val alternativeSelectors =
            listOf(
                "article p",
                ".article-content p",
                ".content p",
                ".detail-text",
            )

        for (selector in alternativeSelectors) {
            val elements = document.select(selector)
            if (elements.isNotEmpty()) {
                return elements.joinToString("\n\n") { it.text().trim() }
            }
        }

        return document.select(".detail-text, .article, .content, main").text().trim()
    }
}
