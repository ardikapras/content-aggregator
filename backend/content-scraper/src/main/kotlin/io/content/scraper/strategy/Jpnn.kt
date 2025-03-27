package io.content.scraper.strategy

import io.content.scraper.models.ProcessingResult
import org.jsoup.nodes.Document

class Jpnn : NewsParsingStrategy {
    private val elementsToRemove =
        mapOf(
            "advertisements" to
                listOf(
                    "div[id*=div-gpt-ad]",
                    "ins.adsbygoogle",
                    "script",
                ),
            "relatedContent" to
                listOf(
                    "div.baca-juga",
                    ".terkait",
                ),
            "socialSharing" to
                listOf(
                    ".top-medsos",
                    ".creator-medsos",
                ),
            "navigation" to
                listOf(
                    ".tags",
                    ".pagination",
                ),
            "comments" to
                listOf(
                    "#disqus_thread",
                    "#disqus-comments",
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

    private fun getAuthor(document: Document): String {
        document.select("meta[name=jpnncom_news_author]").firstOrNull()?.let {
            val content = it.attr("content")
            if (content.isNotBlank()) {
                return content.trim()
            }
        }

        return "JPNN.com"
    }

    private fun getArticle(document: Document): String {
        val contentDiv =
            document.select("div[itemprop=articleBody]").firstOrNull()
                ?: document.select(".page-content").first()

        if (contentDiv != null) {
            val paragraphs = contentDiv.select("p")

            val filteredParagraphs =
                paragraphs.filter { paragraph ->
                    val text = paragraph.text().trim()
                    !text.startsWith("jabar.jpnn.com") &&
                        // Remove domain intro
                        !text.startsWith("JPNN.com") &&
                        // Remove domain intro
                        !text.contains("baca juga", ignoreCase = true) &&
                        // Remove "read also" suggestions
                        !text.matches(Regex("^Baca berita.*")) &&
                        // Remove "read news" suggestions
                        !text.matches(Regex("^Silakan baca.*")) &&
                        // Remove other reading suggestions
                        text.isNotBlank()
                }

            return filteredParagraphs.joinToString("\n\n") { it.text().trim() }
        }

        return document.select(".content, main, article").text().trim()
    }
}
