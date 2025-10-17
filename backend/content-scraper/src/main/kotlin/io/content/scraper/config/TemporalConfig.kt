package io.content.scraper.config

import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowClientOptions
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.serviceclient.WorkflowServiceStubsOptions
import io.temporal.worker.Worker
import io.temporal.worker.WorkerFactory
import io.temporal.worker.WorkerOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class TemporalConfig {
    companion object {
        const val DAILY_AGGREGATION_TASK_QUEUE = "DAILY_AGGREGATION_TASK_QUEUE"
        const val SOURCE_COLLECTION_TASK_QUEUE = "SOURCE_COLLECTION_TASK_QUEUE"
        const val ARTICLE_PROCESSING_TASK_QUEUE = "ARTICLE_PROCESSING_TASK_QUEUE"
    }

    @Bean
    fun workflowServiceStubs(
        @Value("\${spring.temporal.connection.target:127.0.0.1:7233}") target: String,
    ): WorkflowServiceStubs =
        WorkflowServiceStubs.newServiceStubs(
            WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(target)
                .build(),
        )

    @Bean
    fun workflowClient(
        workflowServiceStubs: WorkflowServiceStubs,
        @Value("\${spring.temporal.namespace:default}") namespace: String,
    ): WorkflowClient =
        WorkflowClient.newInstance(
            workflowServiceStubs,
            WorkflowClientOptions
                .newBuilder()
                .setNamespace(namespace)
                .build(),
        )

    @Bean
    fun workerFactory(workflowClient: WorkflowClient): WorkerFactory = WorkerFactory.newInstance(workflowClient)

    @Bean
    fun dailyAggregationWorker(workerFactory: WorkerFactory): Worker = workerFactory.newWorker(DAILY_AGGREGATION_TASK_QUEUE)

    @Bean
    fun sourceCollectionWorker(workerFactory: WorkerFactory): Worker = workerFactory.newWorker(SOURCE_COLLECTION_TASK_QUEUE)

    @Bean
    fun articleProcessingWorker(workerFactory: WorkerFactory): Worker {
        val workerOptions =
            WorkerOptions
                .newBuilder()
                .setDefaultDeadlockDetectionTimeout(Duration.ofMinutes(5).toMillis())
                .build()
        return workerFactory.newWorker(ARTICLE_PROCESSING_TASK_QUEUE, workerOptions)
    }
}
