package io.content.scraper.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import io.content.scraper.constant.KafkaTopics.CONTENT_TO_SCRAPE
import io.content.scraper.dto.ArticleDto
import io.content.scraper.models.Article
import io.content.scraper.models.Source
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.repository.SourceRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.net.URI
import java.time.ZoneId

@Service
class CollectorService(
    private val sourceRepository: SourceRepository,
    private val articleRepository: ArticleRepository,
    private val applicationScope: CoroutineScope,
    private val objectMapper: ObjectMapper,
    private val kafkaTemplate: KafkaTemplate<String, String>,
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
            }

            articleCountsBySource
        }

    private fun Source.fetchFeed(): SyndFeed? {
        val feedUrl = URI.create(this.url).toURL()
        val connection = feedUrl.openConnection()
        connection.connect()

        val inputStream = connection.getInputStream()
        val feedInput = SyndFeedInput()

        return feedInput.build(XmlReader(inputStream))
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
        val articleDto = ArticleDto(savedArticle.id, entryLink, source.parsingStrategy)
        kafkaTemplate.send(
            CONTENT_TO_SCRAPE,
            savedArticle.id.toString(),
            objectMapper.writeValueAsString(articleDto),
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
}
