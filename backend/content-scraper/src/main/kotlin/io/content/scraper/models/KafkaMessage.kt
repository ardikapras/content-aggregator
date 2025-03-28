package io.content.scraper.models

import java.util.UUID

data class KafkaMessage(
    val id: UUID,
    val url: String,
    val parsingStrategy: String,
)
