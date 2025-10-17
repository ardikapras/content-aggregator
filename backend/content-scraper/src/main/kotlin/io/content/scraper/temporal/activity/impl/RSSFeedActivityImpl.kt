package io.content.scraper.temporal.activity.impl

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import io.content.scraper.models.Article
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.repository.SourceRepository
import io.content.scraper.temporal.activity.RSSFeedActivity
import io.content.scraper.temporal.types.ArticleDiscoveryInfo
import io.content.scraper.temporal.types.FeedFetchResult
import io.content.scraper.temporal.types.NewArticleData
import io.github.oshai.kotlinlogging.KotlinLogging
import io.temporal.activity.Activity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.io.StringReader
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

@Component
class RSSFeedActivityImpl(
    private val sourceRepository: SourceRepository,
    private val articleRepository: ArticleRepository,
    private val webClient: WebClient,
) : RSSFeedActivity {
    private val logger = KotlinLogging.logger {}

    override fun fetchRSSFeed(sourceId: UUID): FeedFetchResult {
        Activity.getExecutionContext().heartbeat("Fetching RSS feed for source: $sourceId")

        return try {
            val source =
                sourceRepository.findById(sourceId).orElse(null)
                    ?: return FeedFetchResult(false, null, "Source not found: $sourceId")

            val response =
                webClient
                    .get()
                    .uri(source.url)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .timeout(Duration.ofSeconds(15))
                    .block()

            FeedFetchResult(true, response, null)
        } catch (e: Exception) {
            logger.error(e) { "Error fetching RSS feed for source: $sourceId" }
            FeedFetchResult(false, null, e.message)
        }
    }

    override fun extractArticleUrls(
        feedContent: String,
        sourceUrl: String,
    ): List<ArticleDiscoveryInfo> =
        try {
            val feedInput = SyndFeedInput()
            val feed: SyndFeed = feedInput.build(StringReader(feedContent))

            feed.entries.map { entry ->
                ArticleDiscoveryInfo(
                    title = entry.title ?: "Untitled",
                    url = getAbsoluteLink(entry.link, sourceUrl),
                    description = entry.description?.value ?: "",
                    publishDate =
                        entry.publishedDate
                            ?.toInstant()
                            ?.atZone(ZoneId.of("Asia/Jakarta"))
                            ?.toLocalDateTime(),
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "Error parsing RSS feed content" }
            emptyList()
        }

    override fun saveNewArticles(
        articles: List<NewArticleData>,
        sourceId: UUID,
    ): List<UUID> {
        val source =
            sourceRepository.findById(sourceId).orElse(null)
                ?: return emptyList()

        val newArticleIds = mutableListOf<UUID>()

        articles.forEach { articleData ->
            if (articleRepository.findByUrl(articleData.url) == null) {
                val article =
                    Article(
                        title = articleData.title,
                        description = articleData.description,
                        url = articleData.url,
                        publishDate = articleData.publishDate,
                        source = source,
                    )

                val savedArticle = articleRepository.saveAndFlush(article)
                newArticleIds.add(savedArticle.id)
                logger.debug { "Saved new article: ${savedArticle.id}" }
            }
        }

        return newArticleIds
    }

    override fun updateSourceLastScraped(
        sourceId: UUID,
        timestamp: LocalDateTime,
    ) {
        sourceRepository.findById(sourceId).ifPresent { source ->
            sourceRepository.save(source.copy(lastScraped = timestamp))
            logger.debug { "Updated last scraped time for source: $sourceId" }
        }
    }

    private fun getAbsoluteLink(
        link: String,
        baseUrl: String,
    ): String =
        if (link.startsWith("http://") || link.startsWith("https://")) {
            link
        } else {
            baseUrl + link
        }
}
