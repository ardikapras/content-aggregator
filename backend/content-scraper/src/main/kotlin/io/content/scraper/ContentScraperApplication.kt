package io.content.scraper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ContentScraperApplication

fun main(args: Array<String>) {
    runApplication<ContentScraperApplication>(*args)
}
