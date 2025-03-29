package io.content.scraper.repository

import io.content.scraper.models.Source
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SourceRepository : JpaRepository<Source, UUID> {
    /**
     * Find all active sources
     */
    fun findByIsActiveTrue(): List<Source>

    /**
     * Count active sources
     */
    fun countByIsActiveTrue(): Long

    /**
     * Find the most recently scraped source
     */
    fun findTopByOrderByLastScrapedDesc(): Source?
}
