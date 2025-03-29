package io.content.scraper.repository

import io.content.scraper.api.dto.dashboard.DateCountResult
import io.content.scraper.models.Article
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface ArticleRepository :
    JpaRepository<Article, UUID>,
    JpaSpecificationExecutor<Article> {
    fun findByUrl(url: String): Article?

    fun findByStatusAndRetryCountLessThan(
        status: String,
        maxRetryCount: Int,
    ): List<Article>

    fun findBySourceId(
        sourceId: UUID,
        pageable: Pageable,
    ): Page<Article>

    fun findBySourceNameContainingIgnoreCase(
        sourceName: String,
        pageable: Pageable,
    ): Page<Article>

    fun findByPublishDateBetween(
        fromDate: LocalDateTime,
        toDate: LocalDateTime,
        pageable: Pageable,
    ): Page<Article>

    @Query(
        """
        SELECT a FROM Article a 
        WHERE (LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) 
           OR LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(a.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(a.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
    """,
    )
    fun findBySearchTerm(
        @Param("searchTerm") searchTerm: String,
        pageable: Pageable,
    ): Page<Article>

    fun countByCreatedAtAfter(date: LocalDateTime): Long

    fun countBySourceId(sourceId: UUID): Long

    @Query(
        """
        SELECT CAST(DATE(a.createdAt) AS date) as date, COUNT(a.id) as count 
        FROM Article a 
        WHERE DATE(a.createdAt) BETWEEN :startDate AND :endDate 
        GROUP BY DATE(a.createdAt) 
        ORDER BY date
    """,
    )
    fun countByCreatedAtDateBetween(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
    ): List<DateCountResult>

    @Query(
        """
        SELECT CAST(DATE(a.publishDate) AS date) as date, COUNT(a.id) as count 
        FROM Article a 
        WHERE DATE(a.publishDate) BETWEEN :startDate AND :endDate 
        GROUP BY DATE(a.publishDate) 
        ORDER BY date
    """,
    )
    fun countByPublishDateBetween(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
    ): List<DateCountResult>
}
