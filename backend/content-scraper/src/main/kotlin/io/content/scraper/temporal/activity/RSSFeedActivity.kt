package io.content.scraper.temporal.activity

import io.content.scraper.temporal.types.ArticleDiscoveryInfo
import io.content.scraper.temporal.types.FeedFetchResult
import io.content.scraper.temporal.types.NewArticleData
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod
import java.time.LocalDateTime
import java.util.UUID

@ActivityInterface
interface RSSFeedActivity {
    @ActivityMethod
    fun fetchRSSFeed(sourceId: UUID): FeedFetchResult

    @ActivityMethod
    fun extractArticleUrls(
        feedContent: String,
        sourceUrl: String,
    ): List<ArticleDiscoveryInfo>

    @ActivityMethod
    fun saveNewArticles(
        articles: List<NewArticleData>,
        sourceId: UUID,
    ): List<UUID>

    @ActivityMethod
    fun updateSourceLastScraped(
        sourceId: UUID,
        timestamp: LocalDateTime,
    )
}
