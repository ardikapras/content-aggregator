package io.content.scraper.temporal.worker

import io.content.scraper.temporal.activity.impl.ActivityLogActivityImpl
import io.content.scraper.temporal.activity.impl.AnalyticsActivityImpl
import io.content.scraper.temporal.activity.impl.ContentActivityImpl
import io.content.scraper.temporal.activity.impl.DataActivityImpl
import io.content.scraper.temporal.activity.impl.FeedActivityImpl
import io.content.scraper.temporal.workflow.impl.ArticleProcessingWorkflowImpl
import io.content.scraper.temporal.workflow.impl.DailyAggregationWorkflowImpl
import io.content.scraper.temporal.workflow.impl.SourceCollectionWorkflowImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import io.temporal.worker.Worker
import io.temporal.worker.WorkerFactory
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class TemporalWorker(
    private val workerFactory: WorkerFactory,
    @Qualifier("dailyAggregationWorker") private val dailyAggregationWorker: Worker,
    @Qualifier("sourceCollectionWorker") private val sourceCollectionWorker: Worker,
    @Qualifier("articleProcessingWorker") private val articleProcessingWorker: Worker,
    private val feedActivity: FeedActivityImpl,
    private val contentActivity: ContentActivityImpl,
    private val dataActivity: DataActivityImpl,
    private val analyticsActivity: AnalyticsActivityImpl,
    private val activityLogActivity: ActivityLogActivityImpl,
) {
    private val logger = KotlinLogging.logger {}

    @PostConstruct
    fun startWorkers() {
        logger.info { "Starting Temporal workers..." }

        // Register workflows and activities for daily aggregation
        dailyAggregationWorker.registerWorkflowImplementationTypes(DailyAggregationWorkflowImpl::class.java)
        dailyAggregationWorker.registerActivitiesImplementations(
            dataActivity,
            analyticsActivity,
            activityLogActivity,
        )

        // Register workflows and activities for source collection
        sourceCollectionWorker.registerWorkflowImplementationTypes(SourceCollectionWorkflowImpl::class.java)
        sourceCollectionWorker.registerActivitiesImplementations(
            feedActivity,
            dataActivity,
            activityLogActivity,
        )

        // Register workflows and activities for article processing
        articleProcessingWorker.registerWorkflowImplementationTypes(ArticleProcessingWorkflowImpl::class.java)
        articleProcessingWorker.registerActivitiesImplementations(
            contentActivity,
            dataActivity,
            analyticsActivity,
            activityLogActivity,
        )

        workerFactory.start()
        logger.info { "Temporal workers started successfully" }
    }

    @PreDestroy
    fun stopWorkers() {
        logger.info { "Stopping Temporal workers..." }
        workerFactory.shutdown()
        logger.info { "Temporal workers stopped" }
    }
}
