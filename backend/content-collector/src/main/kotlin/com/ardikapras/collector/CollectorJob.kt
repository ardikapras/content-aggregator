package com.ardikapras.collector

import kotlinx.coroutines.*

class CollectorJob(private val task: CollectorService) {
    var job: Job? = null
        private set

    fun start() {
        if (job == null || job?.isActive == false) {
            job = CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    task.perform()
                    delay(10_000L) // 10 seconds
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    suspend fun triggerManually() {
        task.perform()
    }
}