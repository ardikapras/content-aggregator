package io.content.scraper.repository

import io.content.scraper.models.ParserConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for accessing ParserConfig entities
 */
@Repository
interface ParserConfigRepository : JpaRepository<ParserConfig, UUID> {
    /**
     * Find parser configuration by name
     */
    fun findByName(name: String): ParserConfig?
}
