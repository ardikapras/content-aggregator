package io.content.scraper.strategy

import io.content.scraper.models.ProcessingResult
import io.content.scraper.util.ParserUtils.parseAuthorFromLdJson
import org.jsoup.nodes.Document

class AntaraNews : NewsParsingStrategy {
    override fun parse(document: Document): ProcessingResult<Map<String, String>, String> {
        try {
            val author = getAuthor(document)
            val content = getArticle(document)
            return ProcessingResult.success(mapOf("author" to author, "content" to content))
        } catch (e: Exception) {
            return ProcessingResult.failure(e.message ?: "Failed to extract content")
        }
    }

    private fun getArticle(doc: Document): String {
        // Remove all "baca juga" sections
        doc.select("span.baca-juga").remove()
        doc.select("p:has(span.baca-juga)").remove()

        // Remove all ad sections
        doc.select("ins.adsbygoogle").remove()
        doc.select("script").remove()
        doc.select("p:has(script)").remove()
        doc.select("p:has(ins.adsbygoogle)").remove()

        // Get main content
        val contentDiv = doc.select("div.post-content")
        // Remove the last paragraph which contains metadata
        contentDiv.select("p.text-muted").remove()
        contentDiv.select("ul.blog-tags").remove()
        return contentDiv.text().trim()
    }

    private fun getAuthor(doc: Document): String = parseAuthorFromLdJson(doc, "Antara News")
}
