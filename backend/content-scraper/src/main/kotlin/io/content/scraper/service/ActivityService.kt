package io.content.scraper.service

import io.content.scraper.models.ActivityLog
import io.content.scraper.repository.ActivityLogRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class ActivityService(
    private val activityLogRepository: ActivityLogRepository,
) {
    /**
     * Log a scraper activity
     */
    fun logActivity(
        action: String,
        status: String,
        details: String? = null,
    ): ActivityLog {
        val activityLog =
            ActivityLog(
                id = UUID.randomUUID(),
                timestamp = LocalDateTime.now(),
                action = action,
                status = status,
                details = details,
            )

        return activityLogRepository.save(activityLog)
    }
}
