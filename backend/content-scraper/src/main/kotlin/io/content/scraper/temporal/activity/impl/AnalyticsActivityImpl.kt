package io.content.scraper.temporal.activity.impl

import io.content.scraper.temporal.activity.AnalyticsActivity
import io.content.scraper.temporal.types.AggregationSummary
import io.content.scraper.temporal.types.SourceStatistics
import io.content.scraper.temporal.workflow.SourceCollectionResult
import io.github.oshai.kotlinlogging.KotlinLogging
import io.temporal.activity.Activity
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AnalyticsActivityImpl : AnalyticsActivity {
    private val logger = KotlinLogging.logger {}

    override fun updateArticleMetrics(
        articleId: UUID,
        wordCount: Int,
        readingTimeMinutes: Int,
    ) {
        Activity.getExecutionContext().heartbeat("Updating article metrics: $articleId")

        try {
            // This would typically update analytics/metrics in a database or external system
            logger.debug { "Updated metrics for article $articleId: $wordCount words, $readingTimeMinutes min read time" }
        } catch (e: Exception) {
            logger.error(e) { "Error updating article metrics for: $articleId" }
            throw e
        }
    }

    override fun updateSourceStatistics(
        sourceId: UUID,
        stats: SourceStatistics,
    ) {
        Activity.getExecutionContext().heartbeat("Updating source statistics: $sourceId")

        try {
            // This would typically update source performance metrics
            logger.debug {
                "Updated source statistics for $sourceId: ${stats.totalArticles} articles, ${stats.successfulArticles} successful"
            }
        } catch (e: Exception) {
            logger.error(e) { "Error updating source statistics for: $sourceId" }
            throw e
        }
    }

    override fun generateDailyAggregationSummary(results: Map<String, SourceCollectionResult>): AggregationSummary {
        Activity.getExecutionContext().heartbeat("Generating daily aggregation summary")

        return try {
            val totalSources = results.size
            val totalArticles = results.values.sumOf { it.articlesProcessed }
            val successfulArticles = results.values.sumOf { it.articlesSuccessful }
            val failedArticles = results.values.sumOf { it.articlesFailed }

            val successRate =
                if (totalArticles > 0) {
                    (successfulArticles.toDouble() / totalArticles.toDouble()) * 100
                } else {
                    0.0
                }

            val totalProcessingTime = results.values.sumOf { it.executionTimeMinutes }

            val topPerformingSources =
                results.entries
                    .filter { it.value.articlesSuccessful > 0 }
                    .sortedByDescending { it.value.articlesSuccessful }
                    .take(5)
                    .map { it.key }

            val failedSources =
                results.entries
                    .filter { !it.value.feedFetchSuccess || it.value.articlesFailed > it.value.articlesSuccessful }
                    .map { it.key }

            AggregationSummary(
                totalSources = totalSources,
                totalArticles = totalArticles,
                successRate = successRate,
                totalProcessingTime = totalProcessingTime,
                topPerformingSources = topPerformingSources,
                failedSources = failedSources,
            )
        } catch (e: Exception) {
            logger.error(e) { "Error generating daily aggregation summary" }
            throw e
        }
    }
}
