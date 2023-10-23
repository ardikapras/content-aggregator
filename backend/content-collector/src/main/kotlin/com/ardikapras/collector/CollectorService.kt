package com.ardikapras.collector

import com.ardikapras.dao.NewsSource
import com.ardikapras.util.KafkaTopics.CONTENT_TO_SCRAPE
import com.ardikapras.util.logger
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.net.URL
import java.security.MessageDigest
import java.time.ZoneId
import java.util.*

class CollectorService(private val repository: CollectorRepository, private val producerProps: Properties) {
    private val producer = KafkaProducer<String, String>(producerProps)
    fun perform() {
        repository.getAllActiveNewsSources().forEach { newsSource ->
            CoroutineScope(Dispatchers.IO).launch {
                newsSource.fetchFeed()?.let { feed ->
                    processFeed(newsSource, feed)
                }
            }
        }
    }

    private fun NewsSource.fetchFeed(): SyndFeed? {
        val feedUrl = URL(this.endpointUrl)
        val connection = feedUrl.openConnection()
        connection.connect()

        val inputStream = connection.getInputStream()
        val feedInput = SyndFeedInput()

        return feedInput.build(XmlReader(inputStream))
    }

    private fun processFeed(newsSource: NewsSource, feed: SyndFeed) {
        logger.info("process feed: ${newsSource.endpointUrl}")

        feed.entries.forEach { entry ->
            processEntry(newsSource, entry)
        }
    }

    private fun processEntry(newsSource: NewsSource, entry: SyndEntry) {
        val hash = computeHash(entry.title + entry.link)
        if (!repository.isNewsItemExist(hash)) {
            logger.info("process entry: ${entry.title}")
            val newsSourceId = newsSource.id.value
            val newsItemId = repository.insertNewsItem(
                newsSourceId,
                entry.title,
                entry.link,
                entry.description.value,
                entry.publishedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                hash
            )
            val newsItem = NewsItemDto(newsItemId, entry.link)
            val record = ProducerRecord(CONTENT_TO_SCRAPE, newsSourceId.toString(), Json.encodeToString(newsItem))
            producer.send(record)
        }
    }

    private fun computeHash(content: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(content.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
