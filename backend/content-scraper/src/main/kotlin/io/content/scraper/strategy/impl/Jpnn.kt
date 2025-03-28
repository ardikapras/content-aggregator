package io.content.scraper.strategy.impl

import io.content.scraper.strategy.base.BaseNewsParsingStrategy
import io.content.scraper.strategy.util.CommonElements
import io.content.scraper.strategy.util.ParserUtils
import org.jsoup.nodes.Document

class Jpnn : BaseNewsParsingStrategy() {
    override fun cleanDocument(document: Document) {
        ParserUtils.cleanDocument(document, CommonElements.COMMON_ELEMENTS_TO_REMOVE)
    }

    override fun getAuthor(document: Document): String =
        ParserUtils.parseAuthorFromMeta(
            document,
            listOf("jpnncom_news_author"),
            "JPNN.com",
        )

    override fun getArticle(document: Document): String {
        val contentDiv =
            document.select("div[itemprop=articleBody]").firstOrNull()
                ?: document.select(".page-content").first()

        if (contentDiv != null) {
            val paragraphs = contentDiv.select("p")
            val filteredParagraphs = ParserUtils.filterParagraphs(paragraphs, CommonElements.COMMON_TEXT_EXCLUDES)
            return filteredParagraphs.joinToString("\n\n") { it.text().trim() }
        }

        return ParserUtils.extractFromSelectors(document, CommonElements.COMMON_CONTENT_SELECTORS)
    }
}
