package com.ardikapras

import com.ardikapras.collector.CollectorJob
import com.ardikapras.collector.CollectorRepository
import com.ardikapras.collector.CollectorService
import com.ardikapras.collector.configureRouting
import com.ardikapras.config.DatabaseConfig
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    DatabaseConfig.init()

    val collectorRepository = CollectorRepository()
    val collectorService = CollectorService(collectorRepository)
    val collectorJob = CollectorJob(collectorService)
    configureRouting(collectorJob)
}
