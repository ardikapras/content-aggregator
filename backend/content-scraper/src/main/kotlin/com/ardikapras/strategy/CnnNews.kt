package com.ardikapras.strategy

import com.ardikapras.models.ParsedNews
import org.jsoup.nodes.Document

class CnnNews : NewsParsingStrategy {
    override fun parse(document: Document): ParsedNews {
        val title = getTitle(document)
        val articleText = getArticle(document)
        return ParsedNews(title, articleText)
    }

    private fun getTitle(document: Document): String =  document.select("h1.text-cnn_black").first()?.text() ?: "Title not found"

    private fun getArticle(document: Document): String {
        val contentElement = document.select("article")

        // Extract the news content
        contentElement.select("div.paradetail").remove()
        contentElement.select("table.topiksisip").remove()

        return contentElement.select("div.detail-text > p, div.detail-text > h2").joinToString("\n") { it.text() }
    }
}