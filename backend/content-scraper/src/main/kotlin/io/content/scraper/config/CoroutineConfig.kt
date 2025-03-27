package io.content.scraper.config

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextClosedEvent
import kotlin.time.Duration.Companion.seconds

@Configuration
class CoroutineConfig {
    private val logger = KotlinLogging.logger {}

    @Bean
    fun applicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Bean
    fun coroutineShutdown(applicationScope: CoroutineScope): ApplicationListener<ContextClosedEvent> =
        ApplicationListener {
            logger.info { "Cancelling coroutine scope..." }
            applicationScope.cancel("Application is shutting down")

            try {
                Thread.sleep(5.seconds.inWholeMilliseconds)
                logger.info { "Coroutine scope cancelled successfully." }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                logger.warn { "Interrupted while waiting for coroutines to cancel" }
            }
        }
}
