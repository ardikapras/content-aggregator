package io.content.scraper.config

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.atomic.AtomicLong

@Configuration
class MetricsConfig {
    @Bean
    fun articlesProcessedCounter(meterRegistry: MeterRegistry): Counter =
        Counter
            .builder("articles.processed.total")
            .description("Total number of articles processed")
            .tag("application", "content-scraper")
            .register(meterRegistry)

    @Bean
    fun articlesFailedCounter(meterRegistry: MeterRegistry): Counter =
        Counter
            .builder("articles.failed.total")
            .description("Total number of articles that failed processing")
            .tag("application", "content-scraper")
            .register(meterRegistry)

    @Bean
    fun sourcesScrapedCounter(meterRegistry: MeterRegistry): Counter =
        Counter
            .builder("sources.scraped.total")
            .description("Total number of sources scraped")
            .tag("application", "content-scraper")
            .register(meterRegistry)

    @Bean
    fun articleProcessingTimer(meterRegistry: MeterRegistry): Timer =
        Timer
            .builder("articles.processing.duration")
            .description("Time taken to process an article")
            .tag("application", "content-scraper")
            .register(meterRegistry)

    @Bean
    fun sourceScrapingTimer(meterRegistry: MeterRegistry): Timer =
        Timer
            .builder("sources.scraping.duration")
            .description("Time taken to scrape a source")
            .tag("application", "content-scraper")
            .register(meterRegistry)

    @Bean
    fun temporalWorkflowExecutedCounter(meterRegistry: MeterRegistry): Counter =
        Counter
            .builder("temporal.workflows.executed.total")
            .description("Total number of Temporal workflows executed")
            .tag("application", "content-scraper")
            .register(meterRegistry)

    @Bean
    fun temporalActivityExecutedCounter(meterRegistry: MeterRegistry): Counter =
        Counter
            .builder("temporal.activities.executed.total")
            .description("Total number of Temporal activities executed")
            .tag("application", "content-scraper")
            .register(meterRegistry)

    @Bean
    fun pendingArticlesGauge(meterRegistry: MeterRegistry): AtomicLong {
        val pendingArticles = AtomicLong(0)
        Gauge
            .builder("articles.pending.count", pendingArticles) { it.get().toDouble() }
            .description("Number of articles pending processing")
            .tag("application", "content-scraper")
            .register(meterRegistry)
        return pendingArticles
    }

    @Bean
    fun activeSourcesGauge(meterRegistry: MeterRegistry): AtomicLong {
        val activeSources = AtomicLong(0)
        Gauge
            .builder("sources.active.count", activeSources) { it.get().toDouble() }
            .description("Number of active sources")
            .tag("application", "content-scraper")
            .register(meterRegistry)
        return activeSources
    }
}
