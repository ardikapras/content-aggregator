package io.content.scraper.dto

import java.util.UUID

data class ArticleDto(
    val id: UUID,
    val url: String,
    val parsingStrategy: String,
)
