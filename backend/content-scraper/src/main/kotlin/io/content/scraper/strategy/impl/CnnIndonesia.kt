package io.content.scraper.strategy.impl

import io.content.scraper.strategy.base.BaseNewsParsingStrategy
import io.content.scraper.strategy.util.ParserUtils
import org.jsoup.nodes.Document

class CnnIndonesia : BaseNewsParsingStrategy() {
    private val contentSelectors =
        listOf(
            ".detail-wrap p",
            ".content-artikel p",
            ".content p",
            "article p",
        )

    override fun cleanDocument(document: Document) {
        super.cleanDocument(document)
        document.select(".para_caption").remove()
    }

    override fun getAuthor(document: Document): String =
        ParserUtils.parseAuthorFromMeta(
            document,
            listOf("author", "content_author"),
            "",
        )

    override fun getArticle(document: Document): String {
        val contentDiv = document.select(".detail-text").first()

        if (contentDiv != null) {
            val paragraphs = contentDiv.select("p")
            val filteredParagraphs = paragraphs.filter { element -> !element.hasClass("para_caption") }
            return filteredParagraphs.joinToString("\n\n") { it.text().trim() }
        }

        return ParserUtils.extractFromSelectors(document, contentSelectors)
    }
}
