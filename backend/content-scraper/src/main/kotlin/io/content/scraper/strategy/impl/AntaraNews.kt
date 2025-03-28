package io.content.scraper.strategy.impl

import io.content.scraper.strategy.base.BaseNewsParsingStrategy
import io.content.scraper.strategy.util.ParserUtils.parseAuthorFromLdJson
import org.jsoup.nodes.Document

class AntaraNews : BaseNewsParsingStrategy() {
    override fun cleanDocument(document: Document) {
        super.cleanDocument(document)
        // Remove specific Antara News elements
        document.select("span.baca-juga").remove()
        document.select("p:has(span.baca-juga)").remove()
        document.select("p.text-muted").remove()
        document.select("ul.blog-tags").remove()
    }

    override fun getAuthor(document: Document): String = parseAuthorFromLdJson(document, "Antara News")

    override fun getArticle(document: Document): String {
        val contentDiv = document.select("div.post-content")
        return contentDiv.text().trim()
    }
}
