package com.ardikapras.strategy

import com.ardikapras.models.ParsedNews
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class JpnnNews : NewsParsingStrategy {
    override fun parse(document: Document): ParsedNews {
        val title = getTitle(document)
        val articleText = getArticle(document)
        return ParsedNews(title, articleText)
    }

    private fun getTitle(document: Document): String =  document.select("h1.judul").first()?.text() ?: "Title not found"

    private fun getArticle(document: Document): String {
        // Extract the news content
        val content = StringBuilder()
        val visitedUrls = mutableSetOf<String>()

        fetchContent(document.location(), content, visitedUrls)

        return content.toString()
    }


    private fun fetchContent(url: String, content: StringBuilder, visitedUrls: MutableSet<String>) {
        if (url in visitedUrls) return

        visitedUrls.add(url)

        val document = Jsoup.connect(url).get()
        val newsContent = document.select("div[itemprop=articleBody] > p").joinToString("\n") { it.text() }
        content.append(newsContent)

        // Fetching next page URLs from the pagination section
        val paginationLinks = document.select(".pagination a[href]")
        for (link in paginationLinks) {
            val nextUrl = link.absUrl("href")
            if (nextUrl != "" && nextUrl !in visitedUrls) {
                fetchContent(nextUrl, content, visitedUrls)
            }
        }
    }
}
