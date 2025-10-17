package io.content.scraper.temporal.activity

import io.content.scraper.temporal.types.ContentMetadata
import io.content.scraper.temporal.types.ContentParsingRequest
import io.content.scraper.temporal.types.ContentParsingResult
import io.content.scraper.temporal.types.MultiPageParsingRequest
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface ContentActivity {
    @ActivityMethod
    fun parseArticleContent(request: ContentParsingRequest): ContentParsingResult

    @ActivityMethod
    fun parseMultiPageArticle(request: MultiPageParsingRequest): ContentParsingResult

    @ActivityMethod
    fun extractMetadata(content: String): ContentMetadata
}
