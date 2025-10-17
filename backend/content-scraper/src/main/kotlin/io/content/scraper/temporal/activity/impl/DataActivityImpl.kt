package io.content.scraper.temporal.activity.impl

import io.content.scraper.enums.ArticleStatus
import io.content.scraper.models.Article
import io.content.scraper.models.Source
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.repository.SourceRepository
import io.content.scraper.temporal.activity.DataActivity
import io.content.scraper.temporal.types.NewArticleData
import io.github.oshai.kotlinlogging.KotlinLogging
import io.temporal.activity.Activity
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class DataActivityImpl(
    private val sourceRepository: SourceRepository,
    private val articleRepository: ArticleRepository,
) : DataActivity {
    private val logger = KotlinLogging.logger {}

    override fun findAllActiveSources(): List<Source> {
        Activity.getExecutionContext().heartbeat("Finding all active sources")
        return sourceRepository.findByIsActiveTrue()
    }

    override fun findSourceById(sourceId: UUID): Source? {
        Activity.getExecutionContext().heartbeat("Finding source by ID: $sourceId")
        return sourceRepository.findById(sourceId).orElse(null)
    }

    override fun findArticleById(articleId: UUID): Article? {
        Activity.getExecutionContext().heartbeat("Finding article by ID: $articleId")
        return articleRepository.findById(articleId).orElse(null)
    }

    override fun articleExistsByUrl(url: String): Boolean {
        Activity.getExecutionContext().heartbeat("Checking if article exists by URL")
        return articleRepository.existsByUrl(url)
    }

    override fun saveNewArticles(
        articles: List<NewArticleData>,
        sourceId: UUID,
    ): List<UUID> {
        Activity.getExecutionContext().heartbeat("Saving ${articles.size} new articles for source: $sourceId")

        return try {
            val source =
                sourceRepository.findById(sourceId).orElse(null)
                    ?: throw IllegalArgumentException("Source not found: $sourceId")

            val savedArticles =
                articles.map { articleData ->
                    val article =
                        Article(
                            title = articleData.title,
                            url = articleData.url,
                            description = articleData.description,
                            source = source,
                            publishDate = articleData.publishDate ?: LocalDateTime.now(),
                            status = ArticleStatus.DISCOVERED.name,
                        )
                    articleRepository.save(article)
                }

            logger.debug { "Saved ${savedArticles.size} articles for source: ${source.name}" }
            savedArticles.map { it.id }
        } catch (e: Exception) {
            logger.error(e) { "Error saving articles for source: $sourceId" }
            throw e
        }
    }

    override fun updateArticleContent(
        articleId: UUID,
        content: String?,
        author: String?,
        wordCount: Int,
        readingTimeMinutes: Int,
        status: String,
        errorMessage: String?,
    ) {
        Activity.getExecutionContext().heartbeat("Updating article content: $articleId")

        try {
            val article =
                articleRepository.findById(articleId).orElse(null)
                    ?: throw IllegalArgumentException("Article not found: $articleId")

            val updatedArticle =
                article.copy(
                    content = content,
                    author = author,
                    wordCount = wordCount,
                    readingTimeMinutes = readingTimeMinutes,
                    status = status,
                    errorMessage = errorMessage,
                    lastAttempt = LocalDateTime.now(),
                )

            articleRepository.save(updatedArticle)
            logger.debug { "Updated article content: $articleId" }
        } catch (e: Exception) {
            logger.error(e) { "Error updating article content: $articleId" }
            throw e
        }
    }

    override fun updateSourceLastScraped(
        sourceId: UUID,
        timestamp: LocalDateTime,
    ) {
        Activity.getExecutionContext().heartbeat("Updating source last scraped: $sourceId")

        try {
            val source =
                sourceRepository.findById(sourceId).orElse(null)
                    ?: throw IllegalArgumentException("Source not found: $sourceId")

            val updatedSource = source.copy(lastScraped = timestamp)
            sourceRepository.save(updatedSource)
            logger.debug { "Updated source last scraped: ${source.name}" }
        } catch (e: Exception) {
            logger.error(e) { "Error updating source last scraped: $sourceId" }
            throw e
        }
    }

    override fun incrementArticleRetryCount(articleId: UUID) {
        Activity.getExecutionContext().heartbeat("Incrementing retry count for article: $articleId")

        try {
            val article =
                articleRepository.findById(articleId).orElse(null)
                    ?: throw IllegalArgumentException("Article not found: $articleId")

            val updatedArticle =
                article.copy(
                    retryCount = article.retryCount + 1,
                    lastAttempt = LocalDateTime.now(),
                )

            articleRepository.save(updatedArticle)
            logger.debug { "Incremented retry count for article: $articleId" }
        } catch (e: Exception) {
            logger.error(e) { "Error incrementing retry count for article: $articleId" }
            throw e
        }
    }

    override fun findPendingArticles(maxRetryCount: Int): List<Article> {
        Activity.getExecutionContext().heartbeat("Finding pending articles with max retry count: $maxRetryCount")
        return articleRepository.findByStatusInAndRetryCountLessThan(
            listOf(ArticleStatus.DISCOVERED.name, ArticleStatus.ERROR_SCRAPE.name),
            maxRetryCount,
        )
    }
}
