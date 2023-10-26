package com.ardikapras.scraper

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.configureRouting(service: ScraperService) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    routing {
        route("v1/content-scraper") {
            get("/reprocess") {
                service.reprocess()
                call.respond("Task reprocessing is completed!")
            }

            get("/contents") {
                call.respond(service.getContents())
            }
        }
    }
}
