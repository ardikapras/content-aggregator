package io.content.scraper.service

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong

@Service
class MetricsService(
    private val meterRegistry: MeterRegistry,
    private val articlesProcessedCounter: Counter,
    private val articlesFailedCounter: Counter,
    private val sourcesScrapedCounter: Counter,
    private val articleProcessingTimer: Timer,
    private val sourceScrapingTimer: Timer,
    private val temporalWorkflowExecutedCounter: Counter,
    private val temporalActivityExecutedCounter: Counter,
    private val pendingArticlesGauge: AtomicLong,
    private val activeSourcesGauge: AtomicLong,
) {
    fun recordArticleProcessed(sourceType: String? = null) {
        if (sourceType != null) {
            Counter
                .builder("articles.processed.total")
                .tag("application", "content-scraper")
                .tag("source_type", sourceType)
                .register(meterRegistry)
                .increment()
        } else {
            articlesProcessedCounter.increment()
        }
    }

    fun recordArticleFailed(
        sourceType: String? = null,
        errorType: String? = null,
    ) {
        if (sourceType != null || errorType != null) {
            val builder =
                Counter
                    .builder("articles.failed.total")
                    .tag("application", "content-scraper")
            sourceType?.let { builder.tag("source_type", it) }
            errorType?.let { builder.tag("error_type", it) }
            builder.register(meterRegistry).increment()
        } else {
            articlesFailedCounter.increment()
        }
    }

    fun recordSourceScraped(sourceType: String? = null) {
        if (sourceType != null) {
            Counter
                .builder("sources.scraped.total")
                .tag("application", "content-scraper")
                .tag("source_type", sourceType)
                .register(meterRegistry)
                .increment()
        } else {
            sourcesScrapedCounter.increment()
        }
    }

    fun recordArticleProcessingTime(
        duration: Duration,
        sourceType: String? = null,
    ) {
        if (sourceType != null) {
            Timer
                .builder("articles.processing.duration")
                .tag("application", "content-scraper")
                .tag("source_type", sourceType)
                .register(meterRegistry)
                .record(duration)
        } else {
            articleProcessingTimer.record(duration)
        }
    }

    fun recordSourceScrapingTime(
        duration: Duration,
        sourceType: String? = null,
    ) {
        if (sourceType != null) {
            Timer
                .builder("sources.scraping.duration")
                .tag("application", "content-scraper")
                .tag("source_type", sourceType)
                .register(meterRegistry)
                .record(duration)
        } else {
            sourceScrapingTimer.record(duration)
        }
    }

    fun recordTemporalWorkflowExecuted(
        workflowType: String,
        status: String = "completed",
    ) {
        Counter
            .builder("temporal.workflows.executed.total")
            .tag("application", "content-scraper")
            .tag("workflow_type", workflowType)
            .tag("status", status)
            .register(meterRegistry)
            .increment()
    }

    fun recordTemporalActivityExecuted(
        activityType: String,
        status: String = "completed",
    ) {
        Counter
            .builder("temporal.activities.executed.total")
            .tag("application", "content-scraper")
            .tag("activity_type", activityType)
            .tag("status", status)
            .register(meterRegistry)
            .increment()
    }

    fun updatePendingArticlesCount(count: Long) {
        pendingArticlesGauge.set(count)
    }

    fun updateActiveSourcesCount(count: Long) {
        activeSourcesGauge.set(count)
    }

    fun <T> timeArticleProcessing(
        sourceType: String? = null,
        operation: () -> T,
    ): T {
        val timer = Timer.start(meterRegistry)
        return try {
            operation()
        } finally {
            val targetTimer =
                if (sourceType != null) {
                    Timer
                        .builder("articles.processing.duration")
                        .tag("application", "content-scraper")
                        .tag("source_type", sourceType)
                        .register(meterRegistry)
                } else {
                    articleProcessingTimer
                }
            timer.stop(targetTimer)
        }
    }

    fun <T> timeSourceScraping(
        sourceType: String? = null,
        operation: () -> T,
    ): T {
        val timer = Timer.start(meterRegistry)
        return try {
            operation()
        } finally {
            val targetTimer =
                if (sourceType != null) {
                    Timer
                        .builder("sources.scraping.duration")
                        .tag("application", "content-scraper")
                        .tag("source_type", sourceType)
                        .register(meterRegistry)
                } else {
                    sourceScrapingTimer
                }
            timer.stop(targetTimer)
        }
    }
}
