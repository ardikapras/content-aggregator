package io.content.scraper.strategy

import io.content.scraper.enum.ParsingStrategy
import io.content.scraper.models.ProcessingResult
import org.jsoup.nodes.Document

fun interface NewsParsingStrategy {
    fun parse(document: Document): ProcessingResult<Map<String, String>, String>
}

object NewsStrategyManager {
    val parseStrategyMap =
        mapOf(
            ParsingStrategy.ANTARA.name to AntaraNews(),
            ParsingStrategy.CNBC.name to CnbcIndonesia(),
            ParsingStrategy.CNN.name to CnnIndonesia(),
            ParsingStrategy.JPNN.name to Jpnn(),
        )
}
