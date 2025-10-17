package io.content.scraper.temporal.activity.impl

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import io.content.scraper.repository.SourceRepository
import io.content.scraper.temporal.activity.FeedActivity
import io.content.scraper.temporal.types.ArticleDiscoveryInfo
import io.content.scraper.temporal.types.FeedFetchResult
import io.github.oshai.kotlinlogging.KotlinLogging
import io.temporal.activity.Activity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.io.StringReader
import java.time.Duration
import java.time.ZoneId
import java.util.UUID

@Component
class FeedActivityImpl(
    private val sourceRepository: SourceRepository,
    private val webClient: WebClient =
        WebClient
            .builder()
            .codecs { it.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) }
            .build(),
) : FeedActivity {
    private val logger = KotlinLogging.logger {}

    override fun fetchRSSFeed(sourceId: UUID): FeedFetchResult {
        Activity.getExecutionContext().heartbeat("Fetching RSS feed for source: $sourceId")

        return try {
            val source =
                sourceRepository.findById(sourceId).orElse(null)
                    ?: return FeedFetchResult(false, null, "Source not found")

            val startTime = System.currentTimeMillis()
            val feedContent =
                webClient
                    .get()
                    .uri(source.url)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .timeout(Duration.ofSeconds(30))
                    .block()

            val responseTime = System.currentTimeMillis() - startTime

            if (feedContent.isNullOrBlank()) {
                return FeedFetchResult(false, null, "Empty response from RSS feed", responseTime)
            }

            logger.debug { "Successfully fetched RSS feed for source: ${source.name}" }
            FeedFetchResult(true, feedContent, null, responseTime)
        } catch (e: Exception) {
            logger.error(e) { "Error fetching RSS feed for source: $sourceId" }
            FeedFetchResult(false, null, e.message ?: "Unknown error fetching RSS feed")
        }
    }

    override fun extractArticleUrls(
        feedContent: String,
        sourceUrl: String,
    ): List<ArticleDiscoveryInfo> {
        Activity.getExecutionContext().heartbeat("Extracting article URLs from RSS feed")

        return try {
            val input = SyndFeedInput()
            val feed: SyndFeed = input.build(StringReader(feedContent))

            feed.entries.mapNotNull { entry ->
                if (entry.link != null && entry.title != null) {
                    ArticleDiscoveryInfo(
                        title = entry.title ?: "",
                        url = entry.link ?: "",
                        description = entry.description?.value ?: "",
                        publishDate =
                            entry.publishedDate
                                ?.toInstant()
                                ?.atZone(ZoneId.systemDefault())
                                ?.toLocalDateTime(),
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error extracting article URLs from RSS feed" }
            emptyList()
        }
    }
}
