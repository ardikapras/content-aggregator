package io.content.scraper.models

import io.content.scraper.enums.ArticleStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "articles", schema = "news")
data class Article(
    @Id
    val id: UUID = UUID.randomUUID(),
    val title: String,
    @Column(columnDefinition = "TEXT")
    val description: String,
    val url: String,
    val publishDate: LocalDateTime? = null,
    val author: String? = null,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id")
    val source: Source,
    @Column(columnDefinition = "TEXT")
    val content: String? = null,
    val wordCount: Int? = null,
    val readingTimeMinutes: Int? = null,
    @Column(name = "sentiment_score")
    val sentiment: Float? = null,
    val errorMessage: String? = null,
    val retryCount: Int = 0,
    val lastAttempt: LocalDateTime = LocalDateTime.now(),
    val status: String = ArticleStatus.DISCOVERED.name,
)
