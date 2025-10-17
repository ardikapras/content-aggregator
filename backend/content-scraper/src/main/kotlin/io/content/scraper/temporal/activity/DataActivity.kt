package io.content.scraper.temporal.activity

import io.content.scraper.models.Article
import io.content.scraper.models.Source
import io.content.scraper.temporal.types.NewArticleData
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod
import java.time.LocalDateTime
import java.util.UUID

@ActivityInterface
interface DataActivity {
    @ActivityMethod
    fun findAllActiveSources(): List<Source>

    @ActivityMethod
    fun findSourceById(sourceId: UUID): Source?

    @ActivityMethod
    fun findArticleById(articleId: UUID): Article?

    @ActivityMethod
    fun articleExistsByUrl(url: String): Boolean

    @ActivityMethod
    fun saveNewArticles(
        articles: List<NewArticleData>,
        sourceId: UUID,
    ): List<UUID>

    @ActivityMethod
    fun updateArticleContent(
        articleId: UUID,
        content: String?,
        author: String?,
        wordCount: Int,
        readingTimeMinutes: Int,
        status: String,
        errorMessage: String? = null,
    )

    @ActivityMethod
    fun updateSourceLastScraped(
        sourceId: UUID,
        timestamp: LocalDateTime,
    )

    @ActivityMethod
    fun incrementArticleRetryCount(articleId: UUID)

    @ActivityMethod
    fun findPendingArticles(maxRetryCount: Int): List<Article>
}
