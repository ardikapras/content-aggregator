package io.content.scraper.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import io.content.scraper.constants.KafkaTopics.CONTENT_TO_SCRAPE
import io.content.scraper.enums.ActivityLogAction
import io.content.scraper.enums.ActivityLogStatus
import io.content.scraper.enums.ArticleStatus
import io.content.scraper.models.Article
import io.content.scraper.models.KafkaMessage
import io.content.scraper.models.Source
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.repository.SourceRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.io.StringReader
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class CollectorService(
    private val sourceRepository: SourceRepository,
    private val articleRepository: ArticleRepository,
    private val activityLogService: ActivityLogService,
    private val applicationScope: CoroutineScope,
    private val objectMapper: ObjectMapper,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val webClient: WebClient,
    @Value("\${app.max-retry-count:3}") private val maxRetryCount: Int,
) {
    private val logger = KotlinLogging.logger {}

    suspend fun start(): Map<String, Int> =
        withContext(applicationScope.coroutineContext) {
            logger.debug { "Starting scraping job" }

            val articleCountsBySource = mutableMapOf<String, Int>()

            sourceRepository.findByIsActiveTrue().forEach { source ->
                articleCountsBySource[source.name] = 0

                source.fetchFeed()?.let { feed ->
                    val count = processFeed(source, feed)
                    articleCountsBySource[source.name] = count
                }
                sourceRepository.save(source.copy(lastScraped = LocalDateTime.now()))
            }
            activityLogService.logActivity(
                ActivityLogAction.SCRAPE.name,
                ActivityLogStatus.SUCCESS.name,
                "Scraped ${articleCountsBySource.values.sum()} articles from ${articleCountsBySource.size} sources",
            )
            logger.debug { "Scraping job completed" }
            articleCountsBySource
        }

    private fun Source.fetchFeed(): SyndFeed? =
        try {
            val response =
                webClient
                    .get()
                    .uri(this.url)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .timeout(Duration.ofSeconds(15))
                    .block()

            val feedInput = SyndFeedInput()
            feedInput.build(response?.let { StringReader(it) })
        } catch (e: Exception) {
            logger.error { "Error fetching feed from ${this.url}: ${e.message}" }
            null
        }

    private fun processFeed(
        source: Source,
        feed: SyndFeed,
    ): Int {
        logger.info { "process feed: ${source.url}" }
        var articleCount = 0
        feed.entries.forEach { entry ->
            if (processEntry(source, entry, feed.link)) {
                articleCount++
            }
        }

        return articleCount
    }

    private fun processEntry(
        source: Source,
        entry: SyndEntry,
        baseUrl: String,
    ): Boolean {
        logger.debug { "Processing entry: ${entry.title}" }
        val entryLink = getAbsoluteLink(entry.link, baseUrl)

        val existingArticle = articleRepository.findByUrl(entryLink)
        if (existingArticle != null) {
            logger.debug { "Article already exists: ${existingArticle.id}" }
            return false
        }

        val article =
            Article(
                title = entry.title,
                description = entry.description?.value ?: "",
                url = entryLink,
                publishDate =
                    entry.publishedDate
                        ?.toInstant()
                        ?.atZone(ZoneId.of("Asia/Jakarta"))
                        ?.toLocalDateTime(),
                source = source,
            )

        val savedArticle = articleRepository.saveAndFlush(article)

        val parserConfig = source.parserConfig
        val parsingStrategy = parserConfig?.name ?: source.parsingStrategy

        val kafkaMessage = KafkaMessage(savedArticle.id, entryLink, parsingStrategy)
        kafkaTemplate.send(
            CONTENT_TO_SCRAPE,
            savedArticle.id.toString(),
            objectMapper.writeValueAsString(kafkaMessage),
        )

        return true
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

    /**
     * Push articles with DISCOVERED status and less than max retry count to Kafka
     * Returns a map of source names to article counts
     */
    fun retryPendingArticles(): Map<String, Int> {
        val pendingArticles =
            articleRepository.findByStatusInAndRetryCountLessThan(
                listOf(ArticleStatus.DISCOVERED.name, ArticleStatus.ERROR_SCRAPE.name),
                maxRetryCount,
            )

        val countBySource = mutableMapOf<String, Int>()

        try {
            pendingArticles.forEach { article ->
                val sourceName = article.source.name
                countBySource[sourceName] = countBySource.getOrDefault(sourceName, 0) + 1

                val parserConfig = article.source.parserConfig
                val parsingStrategy = parserConfig?.name ?: article.source.parsingStrategy

                val kafkaMessage = KafkaMessage(article.id, article.url, parsingStrategy)
                kafkaTemplate.send(
                    CONTENT_TO_SCRAPE,
                    article.id.toString(),
                    objectMapper.writeValueAsString(kafkaMessage),
                )

                logger.debug { "Pushed article ${article.id} to Kafka" }
            }

            activityLogService.logActivity(
                ActivityLogAction.RETRY_SCRAPE.name,
                ActivityLogStatus.SUCCESS.name,
                "Successfully retried ${pendingArticles.size} pending articles",
            )
        } catch (e: Exception) {
            logger.error(e) { "Error retrying pending articles" }

            activityLogService.logActivity(
                ActivityLogAction.RETRY_SCRAPE.name,
                ActivityLogStatus.FAILED.name,
                "Failed to retry pending articles: ${e.message}",
            )

            throw e
        }

        return countBySource
    }
}
