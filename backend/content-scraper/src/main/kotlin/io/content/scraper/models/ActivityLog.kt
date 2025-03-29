package io.content.scraper.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

/**
 * Entity for storing system activity logs
 */
@Entity
@Table(name = "activity_logs", schema = "news")
data class ActivityLog(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false, length = 50)
    val action: String,
    @Column(nullable = false, length = 20)
    val status: String,
    @Column(name = "details", columnDefinition = "TEXT")
    val details: String? = null,
)
