package io.content.scraper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableRetry
class NewsScraperApplication

fun main(args: Array<String>) {
    runApplication<NewsScraperApplication>(*args)
}
