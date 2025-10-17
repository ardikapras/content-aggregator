package io.content.scraper.temporal.activity

import io.content.scraper.temporal.types.ArticleDiscoveryInfo
import io.content.scraper.temporal.types.FeedFetchResult
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod
import java.util.UUID

@ActivityInterface
interface FeedActivity {
    @ActivityMethod
    fun fetchRSSFeed(sourceId: UUID): FeedFetchResult

    @ActivityMethod
    fun extractArticleUrls(
        feedContent: String,
        sourceUrl: String,
    ): List<ArticleDiscoveryInfo>
}
