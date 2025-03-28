package io.content.scraper.parser.util

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

object DocumentFactory {
    /**
     * Creates a Document from HTML string with proper settings for content extraction
     */
    fun fromHtml(html: String): Document {
        val document = Jsoup.parse(html, "", Parser.htmlParser())
        document.outputSettings().prettyPrint(false)
        return document
    }

    /**
     * Creates a Document from a URL with proper settings for content extraction
     */
    fun fromUrl(url: String): Document {
        val connection =
            Jsoup
                .connect(url)
                .parser(Parser.htmlParser())
                .timeout(30000)
        val document = connection.get()
        document.outputSettings().prettyPrint(false)
        return document
    }
}
