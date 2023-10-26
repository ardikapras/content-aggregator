package com.ardikapras.strategy

import com.ardikapras.models.ParsedNews
import org.jsoup.nodes.Document

fun interface NewsParsingStrategy {
    fun parse(document: Document): ParsedNews
}

object NewsStrategyManager {
    val parseStrategyMap = mapOf(
        "ANTARA" to AntaraNews(),
        "CNBC" to CnbcNews()
    )
}