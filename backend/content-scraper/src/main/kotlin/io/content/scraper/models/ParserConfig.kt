package io.content.scraper.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a configurable parser for extracting content from news sources.
 * Parser configs can be created, edited, and tested through the UI.
 */
@Entity
@Table(name = "parser_configs", schema = "news")
data class ParserConfig(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String? = null,
    @Column(name = "author_selectors", columnDefinition = "text[]")
    val authorSelectors: List<String> = emptyList(),
    @Column(name = "content_selectors", columnDefinition = "text[]")
    val contentSelectors: List<String> = emptyList(),
    @Column(name = "next_page_selector")
    val nextPageSelector: String? = null,
    @Column(name = "content_filters", columnDefinition = "text[]")
    val contentFilters: List<String> = emptyList(),
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    /**
     * Creates a builder for ParserConfig
     */
    companion object {
        fun builder(): Builder = Builder()
    }

    /**
     * Builder class for ParserConfig
     */
    class Builder {
        private var id: UUID = UUID.randomUUID()
        private var name: String = ""
        private var description: String? = null
        private var authorSelectors: List<String> = emptyList()
        private var contentSelectors: List<String> = emptyList()
        private var nextPageSelector: String? = null
        private var contentFilters: List<String> = emptyList()

        fun id(id: UUID) = apply { this.id = id }

        fun name(name: String) = apply { this.name = name }

        fun description(description: String?) = apply { this.description = description }

        fun authorSelectors(selectors: List<String>) = apply { this.authorSelectors = selectors }

        fun contentSelectors(selectors: List<String>) = apply { this.contentSelectors = selectors }

        fun nextPageSelector(selector: String?) = apply { this.nextPageSelector = selector }

        fun contentFilters(filters: List<String>) = apply { this.contentFilters = filters }

        fun build() =
            ParserConfig(
                id = id,
                name = name,
                description = description,
                authorSelectors = authorSelectors,
                contentSelectors = contentSelectors,
                nextPageSelector = nextPageSelector,
                contentFilters = contentFilters,
            )
    }
}
