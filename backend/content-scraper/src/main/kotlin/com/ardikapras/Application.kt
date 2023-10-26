package com.ardikapras

import com.ardikapras.config.DatabaseConfig
import com.ardikapras.config.KafkaConfig
import com.ardikapras.constant.KafkaTopics
import com.ardikapras.scraper.ScraperRepository
import com.ardikapras.scraper.ScraperService
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.apache.kafka.clients.consumer.KafkaConsumer

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    DatabaseConfig.init()

    // Start News Consumer Service
    val scraperRepository = ScraperRepository()
    val newsConsumerService = ScraperService(KafkaConsumer<String, String>(KafkaConfig.consumerProps), KafkaTopics.CONTENT_TO_SCRAPE, scraperRepository)
    newsConsumerService.start()

    environment.monitor.subscribe(ApplicationStopping) {
        newsConsumerService.stop()
    }
}
