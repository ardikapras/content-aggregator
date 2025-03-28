package io.content.scraper.strategy.util

import io.content.scraper.enum.ParsingStrategy
import io.content.scraper.strategy.base.NewsParsingStrategy
import io.content.scraper.strategy.impl.AntaraNews
import io.content.scraper.strategy.impl.CnbcIndonesia
import io.content.scraper.strategy.impl.CnnIndonesia
import io.content.scraper.strategy.impl.DefaultStrategy
import io.content.scraper.strategy.impl.Jpnn

object NewsStrategyManager {
    val parseStrategyMap =
        mapOf(
            ParsingStrategy.ANTARA.name to AntaraNews(),
            ParsingStrategy.CNBC.name to CnbcIndonesia(),
            ParsingStrategy.CNN.name to CnnIndonesia(),
            ParsingStrategy.JPNN.name to Jpnn(),
            ParsingStrategy.DEFAULT.name to DefaultStrategy(),
        )

    /**
     * Get strategy by name with fallback to default
     */
    fun getStrategy(strategyName: String): NewsParsingStrategy =
        parseStrategyMap[strategyName] ?: parseStrategyMap[ParsingStrategy.DEFAULT.name]!!
}
