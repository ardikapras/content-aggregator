package io.content.scraper.strategy.impl

import io.content.scraper.strategy.base.BaseNewsParsingStrategy
import io.content.scraper.strategy.util.ParserUtils
import org.jsoup.nodes.Document

class CnbcIndonesia : BaseNewsParsingStrategy() {
    private val cnbcSpecificElements =
        mapOf(
            "agencies" to listOf(".media-institusi"),
        )

    override fun cleanDocument(document: Document) {
        super.cleanDocument(document)
        ParserUtils.cleanDocument(document, cnbcSpecificElements)
    }

    override fun getAuthor(document: Document): String = ParserUtils.parseAuthorFromLdJson(document, "CNBC Indonesia")

    override fun getArticle(document: Document): String {
        val contentDiv = document.select(".detail-text").first()

        if (contentDiv != null) {
            val paragraphs = contentDiv.select("p")
            val filteredParagraphs =
                ParserUtils.filterParagraphs(
                    paragraphs,
                    listOf(Regex("\\([a-z]{2,3}/[a-z]{2,3}\\)")),
                )
            return filteredParagraphs.joinToString("\n\n") { it.text().trim() }
        }

        val alternativeSelectors =
            listOf(
                "article p",
                ".article-content p",
                ".content p",
                ".detail-text",
            )

        return ParserUtils.extractFromSelectors(document, alternativeSelectors)
    }
}
