package com.ardikapras.collector

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class CollectorJobTest {

    private lateinit var collectorService: CollectorService
    private lateinit var collectorJob: CollectorJob

    @BeforeEach
    fun setUp() {
        collectorService = mock()
        collectorJob = CollectorJob(collectorService)
    }

    @AfterEach
    fun tearDown() {
        collectorJob.stop()  // Ensure the coroutine is stopped after each test
    }

    @Test
    fun `start should initiate a coroutine task`() = runBlocking {
        collectorJob.start()
        delay(100L)  // Slight delay to ensure the coroutine has started
        verify(collectorService).perform()

        collectorJob.stop()
        assertFalse(collectorJob.job?.isActive ?: true)
    }

    @Test
    fun `stop should cancel the coroutine task`() = runBlocking {
        collectorJob.start()
        delay(100L)  // Slight delay to ensure the coroutine has started

        collectorJob.stop()
        assertFalse(collectorJob.job?.isActive ?: true)
    }

    @Test
    fun `triggerManually should call perform`() = runBlocking {
        collectorJob.triggerManually()
        verify(collectorService).perform()
    }
}
