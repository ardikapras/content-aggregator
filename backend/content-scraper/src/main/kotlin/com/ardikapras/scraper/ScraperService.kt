package com.ardikapras.scraper

import com.ardikapras.dto.NewsItemDto
import com.ardikapras.strategy.NewsStrategyManager.parseStrategyMap
import com.ardikapras.util.logger
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.jsoup.Jsoup
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

class ScraperService(
    private val consumer: KafkaConsumer<String, String>,
    private val topic: String,
    private val repository: ScraperRepository,
) {
    private val running = AtomicBoolean(true)

    fun start() {
        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("Shutdown initiated")
            running.set(false)
        })

        try {
            consumer.subscribe(listOf(topic))

            while (running.get()) {
                val records = consumer.poll(Duration.ofMillis(100))
                for (record in records) {
                    processRecord(record)
                }
            }
        } catch (e: Exception) {
            logger.error("Error in consuming messages: ${e.message}")
        } finally {
            try {
                consumer.commitSync() // Final commit
            } finally {
                consumer.close()
                logger.info("Consumer closed")
            }
        }
    }

    // Gracefully close the consumer
    fun stop() {
        consumer.close()
    }

    private fun processRecord(record: ConsumerRecord<String, String>) {
        try {
            logger.info("consuming ${record.value()}")
            val newsItemDto = Json.decodeFromString<NewsItemDto>(record.value())
            val newsDocument = Jsoup.connect(newsItemDto.newsItemUrl).get()
            val strategy = parseStrategyMap[newsItemDto.parsingStrategy]
            val parsedNews = strategy?.parse(newsDocument)
                ?: throw IllegalArgumentException("No strategy found for source: ${newsItemDto.parsingStrategy}")
            repository.updateNewsItem(newsItemDto.newsItemId, parsedNews.content)
        } catch (e: Exception) {
            logger.error("Error processing record: ${e.message}")
        }
    }

    fun reprocess() {
        logger.info("Reprocessing failure content")
    }

    fun getContents(): ContentDto {
        logger.info("Get all contents")
        return ContentDto("", "", "")
    }
}