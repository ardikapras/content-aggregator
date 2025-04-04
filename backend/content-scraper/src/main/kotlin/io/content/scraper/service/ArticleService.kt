package io.content.scraper.service

import io.content.scraper.models.Article
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.UUID

interface ArticleService {
    /**
     * Find articles with optional search, filtering and pagination
     *
     * @param searchTerm Optional search term to filter by title, description, content or author
     * @param sourceName Optional source name to filter articles by
     * @param fromDate Optional start date to filter articles by publish date
     * @param toDate Optional end date to filter articles by publish date
     * @param pageable Pagination and sorting information
     * @return A page of articles matching the criteria
     */
    fun findArticles(
        searchTerm: String?,
        sourceName: String?,
        status: String?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
        pageable: Pageable,
    ): Page<Article>

    /**
     * Find an article by its ID
     *
     * @param id The article ID
     * @return The article
     */
    fun findById(id: UUID): Article

    /**
     * Find articles by source ID with pagination
     *
     * @param sourceId The source ID
     * @param pageable Pagination information
     * @return A page of articles
     */
    fun findBySourceId(
        sourceId: UUID,
        pageable: Pageable,
    ): Page<Article>
}
