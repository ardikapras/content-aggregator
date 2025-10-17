package io.content.scraper.temporal.activity

import io.content.scraper.temporal.types.AggregationSummary
import io.content.scraper.temporal.types.SourceStatistics
import io.content.scraper.temporal.workflow.SourceCollectionResult
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod
import java.util.UUID

@ActivityInterface
interface AnalyticsActivity {
    @ActivityMethod
    fun updateArticleMetrics(
        articleId: UUID,
        wordCount: Int,
        readingTimeMinutes: Int,
    )

    @ActivityMethod
    fun updateSourceStatistics(
        sourceId: UUID,
        stats: SourceStatistics,
    )

    @ActivityMethod
    fun generateDailyAggregationSummary(results: Map<String, SourceCollectionResult>): AggregationSummary
}
