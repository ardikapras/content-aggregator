package io.content.scraper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableRetry
class ContentScraperApplication

fun main(args: Array<String>) {
    runApplication<ContentScraperApplication>(*args)
}
