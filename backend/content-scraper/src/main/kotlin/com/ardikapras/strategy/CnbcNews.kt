package com.ardikapras.strategy

import com.ardikapras.models.ParsedNews
import org.jsoup.nodes.Document

class CnbcNews : NewsParsingStrategy {
    override fun parse(document: Document): ParsedNews {
        val title = getTitle(document)
        val articleText = getArticle(document)
        return ParsedNews(title, articleText)
    }

    private fun getTitle(document: Document): String =  document.select("div.jdl > div.container > h1").first()?.text() ?: "Title not found"

    private fun getArticle(document: Document): String {
        val contentElement = document.select("article")

        // Extract the news content
        contentElement.select("div.paradetail").remove()
        contentElement.select("table.linksisip").remove()

        return contentElement.select(".detail_text > p").joinToString("\n") { it.text() }
    }
}