package com.ardikapras.collector

import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import java.net.URL

class CollectorService(private val repository: CollectorRepository) {
    fun perform() {
        val listOfNewsSource = repository.getAllActiveNewsSources()
        listOfNewsSource.forEach {
            val feedUrl = URL(it.endpointUrl)
            val connection = feedUrl.openConnection()

            connection.connect()
            val inputStream = connection.getInputStream()

            val feedInput = SyndFeedInput()
            val feed = feedInput.build(XmlReader(inputStream))

            feed.entries.forEach { entry: SyndEntry -> println(entry.title) }

            inputStream.close()
        }

        println("Scheduled task performed!")
    }
}