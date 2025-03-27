package io.content.scraper.models

import io.content.scraper.enum.ParsingStrategy
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "sources", schema = "news")
data class Source(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val url: String,
    val parsingStrategy: String = ParsingStrategy.DEFAULT.name,
    val lastScraped: LocalDateTime? = null,
    val isActive: Boolean = true,
)
