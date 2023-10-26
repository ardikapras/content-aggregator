package com.ardikapras.strategy

import com.ardikapras.models.ParsedNews
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class AntaraNews : NewsParsingStrategy {
    override fun parse(document: Document): ParsedNews {
        val title = getTitle(document)
        val articleText = getArticle(document)
        return ParsedNews(title, articleText)
    }

    private fun getTitle(document: Document): String = document.select("h1.post-title").text()

    private fun getArticle(document: Document): String {
        val contentElement = document.select("div.post-content")
        // Remove the <span class="baca-juga"> and its content
        contentElement.select("span.baca-juga").remove()

        // Remove the author, editor, copyright notice etc.
        contentElement.select("p.text-muted").remove()

        val articleBody = contentElement.html().replace("<br>", "\n").replace("<br/>", "\n")
        return Jsoup.parse(articleBody).text()
    }
}