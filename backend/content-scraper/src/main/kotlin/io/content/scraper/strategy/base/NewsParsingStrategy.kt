package io.content.scraper.strategy.base

import io.content.scraper.models.ProcessingResult
import org.jsoup.nodes.Document

fun interface NewsParsingStrategy {
    fun parse(document: Document): ProcessingResult<Map<String, String>, String>
}
