package io.content.scraper.repository

import io.content.scraper.models.ActivityLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface ActivityLogRepository : JpaRepository<ActivityLog, UUID> {
    /**
     * Find the most recent activity by action type
     */
    fun findTopByActionOrderByTimestampDesc(action: String): ActivityLog?

    /**
     * Find activity logs between dates, sorted by timestamp descending
     */
    fun findByTimestampBetweenOrderByTimestampDesc(
        start: LocalDateTime,
        end: LocalDateTime,
    ): List<ActivityLog>

    /**
     * Find recent activity logs, limited by count
     */
    fun findTop10ByOrderByTimestampDesc(): List<ActivityLog>

    /**
     * Find activity logs by status
     */
    fun findByStatus(status: String): List<ActivityLog>
}
