package io.content.scraper.models

import io.content.scraper.enums.ParsingStrategy
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parser_config_id")
    val parserConfig: ParserConfig? = null,
    val lastScraped: LocalDateTime? = null,
    val isActive: Boolean = true,
)
