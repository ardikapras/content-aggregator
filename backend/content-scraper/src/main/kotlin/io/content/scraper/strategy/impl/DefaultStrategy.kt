package io.content.scraper.strategy.impl

import io.content.scraper.strategy.base.BaseNewsParsingStrategy
import io.content.scraper.strategy.util.CommonElements
import io.content.scraper.strategy.util.ParserUtils
import org.jsoup.nodes.Document

/**
 * Default fallback strategy with generic parsing
 */
class DefaultStrategy : BaseNewsParsingStrategy() {
    override fun getAuthor(document: Document): String {
        // Try multiple common methods to find author
        val ldJsonAuthor = ParserUtils.parseAuthorFromLdJson(document, "")
        if (ldJsonAuthor.isNotBlank()) return ldJsonAuthor

        return ParserUtils.parseAuthorFromMeta(
            document,
            CommonElements.COMMON_META_AUTHOR_SELECTORS,
            "Unknown",
        )
    }

    override fun getArticle(document: Document): String =
        ParserUtils.extractFromSelectors(document, CommonElements.COMMON_CONTENT_SELECTORS)
}
