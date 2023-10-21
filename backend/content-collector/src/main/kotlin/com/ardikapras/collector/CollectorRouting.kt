package com.ardikapras.collector

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(job: CollectorJob) {
    routing {
        route("v1/content-collector") {
            get("/trigger") {
                job.triggerManually()
                call.respondText("Task triggered manually!")
            }

            get("/stop") {
                job.stop()
                call.respondText("Task stopped!")
            }

            get("/start") {
                job.start()
                call.respondText("Task started!")
            }
        }
    }
}
