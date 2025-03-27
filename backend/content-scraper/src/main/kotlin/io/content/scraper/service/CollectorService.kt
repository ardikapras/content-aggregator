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
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Service
import java.net.URL
import java.time.ZoneId
import java.util.Properties

@Service
class CollectorService(
    private val sourceRepository: SourceRepository,
    private val articleRepository: ArticleRepository,
    private val applicationScope: CoroutineScope,
    private val objectMapper: ObjectMapper,
    producerProperties: Properties,
) {
    private val logger = KotlinLogging.logger {}
    private val producer = KafkaProducer<String, String>(producerProperties)

    suspend fun start() {
        withContext(applicationScope.coroutineContext) {
            logger.debug { "Starting scraping job" }

            sourceRepository.findByIsActiveTrue().forEach { source ->
                source.fetchFeed()?.let { feed ->
                    processFeed(source, feed)
                }
            }
        }
    }

    private fun Source.fetchFeed(): SyndFeed? {
        val feedUrl = URL(this.url)
        val connection = feedUrl.openConnection()
        connection.connect()

        val inputStream = connection.getInputStream()
        val feedInput = SyndFeedInput()

        return feedInput.build(XmlReader(inputStream))
    }

    private fun processFeed(
        source: Source,
        feed: SyndFeed,
    ) {
        logger.info { "process feed: ${source.url}" }

        feed.entries.forEach { entry ->
            processEntry(source, entry, feed.link)
        }
    }

    private fun processEntry(
        source: Source,
        entry: SyndEntry,
        baseUrl: String,
    ) {
        logger.info { "process entry: ${entry.title}" }
        val newsSourceId = source.id
        val entryLink = getAbsoluteLink(entry.link, baseUrl)
        val article =
            articleRepository.saveAndFlush(
                Article(
                    title = entry.title,
                    description = entry.description.value,
                    url = entryLink,
                    publishDate =
                        entry.publishedDate
                            .toInstant()
                            .atZone(ZoneId.of("Asia/Jakarta"))
                            .toLocalDateTime(),
                    source = source,
                ),
            )
        val articleDto = ArticleDto(article.id, entryLink, source.parsingStrategy)
        val record = ProducerRecord(CONTENT_TO_SCRAPE, newsSourceId.toString(), objectMapper.writeValueAsString(articleDto))
        producer.send(record)
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
