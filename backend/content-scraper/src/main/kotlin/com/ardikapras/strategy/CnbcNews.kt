package com.ardikapras.strategy

import com.ardikapras.models.ParsedNews
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class CnbcNews : NewsParsingStrategy {
    override fun parse(document: Document): ParsedNews {
        val url =
            "https://www.antaranews.com/berita/3462036/menko-airlangga-sebut-start-up-dapat-tingkatkan-pertumbuhan-ekonomi"
        val newsDocument = Jsoup.connect(url).get()

        val title = getTitle(newsDocument)
        val articleText = getArticle(newsDocument)
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