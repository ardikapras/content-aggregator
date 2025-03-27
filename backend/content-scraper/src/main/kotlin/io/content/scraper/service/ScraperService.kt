package io.content.scraper.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.content.scraper.dto.ArticleDto
import io.content.scraper.repository.ArticleRepository
import io.content.scraper.strategy.NewsStrategyManager.parseStrategyMap
import io.github.oshai.kotlinlogging.KotlinLogging
import io.news.scraper.core.enum.ArticleStatus
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import java.time.Duration
import java.time.LocalDateTime

class ScraperService(
    private val consumer: KafkaConsumer<String, String>,
    private val topic: String,
    private val articleRepository: ArticleRepository,
    private val objectMapper: ObjectMapper,
    @Value("\${app.max-retry-count:3}") private val maxRetryCount: Int,
) {
    private val logger = KotlinLogging.logger {}

    fun start() {
        try {
            consumer.subscribe(listOf(topic))

            val records = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                processRecord(record)
            }
        } catch (e: Exception) {
            logger.error { "Error in consuming messages: ${e.message}" }
        } finally {
            try {
                consumer.commitSync()
            } finally {
                consumer.close()
                logger.info { "Consumer closed" }
            }
        }
    }

    private fun processRecord(record: ConsumerRecord<String, String>) {
        logger.info { "consuming ${record.value()}" }
        val articleDto = objectMapper.readValue(record.value(), ArticleDto::class.java)
        try {
            articleRepository
                .findByUrl(articleDto.url)
                .takeIf { it != null && it.retryCount < maxRetryCount }
                ?.let {
                    val newsDocument = Jsoup.connect(it.url).get()
                    val strategy = parseStrategyMap[articleDto.parsingStrategy]
                    val parsedNews =
                        strategy?.parse(newsDocument)
                            ?: throw IllegalArgumentException("No strategy found for source: ${articleDto.parsingStrategy}")
                    if (parsedNews.failure) {
                        articleRepository.save(
                            it.copy(
                                errorMessage = parsedNews.failureValue,
                                status = ArticleStatus.ERROR_SCRAPE.name,
                                retryCount = it.retryCount + 1,
                                lastAttempt = LocalDateTime.now(),
                            ),
                        )
                    }
                    articleRepository.save(
                        it.copy(
                            author = parsedNews.successValue["author"],
                            content = parsedNews.successValue["content"],
                            status = ArticleStatus.SCRAPED.name,
                        ),
                    )
                }
        } catch (e: Exception) {
            logger.error { "Error processing record: ${e.message}" }
        }
    }
}
