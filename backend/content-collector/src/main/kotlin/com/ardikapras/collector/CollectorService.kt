package com.ardikapras.collector

import com.ardikapras.dao.NewsSourcesDao
import com.ardikapras.util.logger
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import java.security.MessageDigest
import java.time.ZoneId

class CollectorService(private val repository: CollectorRepository) {
    fun perform() {
        repository.getAllActiveNewsSources().forEach { newsSource ->
            CoroutineScope(Dispatchers.IO).launch {
                newsSource.fetchFeed()?.let { feed ->
                    processFeed(newsSource, feed)
                }
            }
        }
    }

    private fun NewsSourcesDao.fetchFeed(): SyndFeed? {
        val feedUrl = URL(this.endpointUrl)
        val connection = feedUrl.openConnection()
        connection.connect()

        val inputStream = connection.getInputStream()
        val feedInput = SyndFeedInput()

        return feedInput.build(XmlReader(inputStream))
    }

    private fun processFeed(newsSourcesDao: NewsSourcesDao, feed: SyndFeed) {
        logger.info("process feed: ${newsSourcesDao.endpointUrl}")

        feed.entries.forEach { entry ->
            processEntry(newsSourcesDao, entry)
        }
    }

    private fun processEntry(newsSourcesDao: NewsSourcesDao, entry: SyndEntry) {
        val hash = computeHash(entry.title + entry.link)
        if (!repository.isNewsItemExist(hash)) {
            logger.info("process entry: ${entry.title}")
            repository.insertNewsItem(
                newsSourcesDao.id.value,
                entry.title,
                entry.link,
                entry.description.value,
                entry.publishedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                hash
            )
        }
    }

    private fun computeHash(content: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(content.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}