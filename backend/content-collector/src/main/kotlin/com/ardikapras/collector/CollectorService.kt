package com.ardikapras.collector

import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.net.URL
import java.security.MessageDigest
import java.time.ZoneId

class CollectorService(private val repository: CollectorRepository) {
    suspend fun perform() = coroutineScope {
        val listOfNewsSource = repository.getAllActiveNewsSources()
        listOfNewsSource.forEach { newsSource ->
            launch {
                val feedUrl = URL(newsSource.endpointUrl)
                val connection = feedUrl.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                val feedInput = SyndFeedInput()
//                val content = inputStream.bufferedReader().use(BufferedReader::readText)
                val feed = feedInput.build(XmlReader(inputStream))
                val content = feed.toString()
                val hashRssContent = computeHash(content)
                if (!repository.isRawRssDataExist(hashRssContent)) {
                    val rawRssDataId = repository.insertRawRssData(newsSource.id.value, content, hashRssContent)
                    feed.entries.forEach { entry: SyndEntry ->
                        println("processing ${entry.title} - ${entry.link}")
                        val hash = computeHash(entry.title + entry.link)
                        if (!repository.isNewsItemExist(hash)) {
                            repository.insertNewsItem(
                                newsSource.id.value,
                                rawRssDataId,
                                entry.title,
                                entry.link,
                                entry.description.value,
                                entry.publishedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                                hash
                            )
                        }
                    }
                }
                inputStream.close()
            }
        }
    }

    private fun computeHash(content: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(content.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}