package io.content.scraper.util

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.jsoup.nodes.Document

object ParserUtils {
    private val logger = KotlinLogging.logger {}

    fun parseAuthorFromLdJson(
        doc: Document,
        default: String,
    ): String {
        doc.select("script[type=application/ld+json]").forEach { scriptTag ->
            try {
                val jsonContent = scriptTag.html()
                val json = Json { ignoreUnknownKeys = true }
                when (val jsonElement = json.parseToJsonElement(jsonContent)) {
                    is JsonObject -> {
                        val authorElement = jsonElement["author"]
                        if (authorElement is JsonObject && authorElement.containsKey("name")) {
                            return authorElement["name"]?.jsonPrimitive?.contentOrNull ?: ""
                        }
                    }

                    else -> return default
                }
            } catch (e: Exception) {
                logger.debug { "Error parsing JSON-LD: ${e.message}" }
            }
        }
        return default
    }
}
