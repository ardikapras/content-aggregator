package io.content.scraper.service

import io.content.scraper.api.dto.ActivityLogDto
import io.content.scraper.repository.ActivityLogRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ActivityLogService(
    private val activityLogRepository: ActivityLogRepository,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Get recent activity logs
     */
    fun getRecentActivityLogs(): List<ActivityLogDto> {
        logger.debug { "Getting recent activity logs" }
        return activityLogRepository
            .findTop10ByOrderByTimestampDesc()
            .map { ActivityLogDto.fromEntity(it) }
    }

    /**
     * Get activity logs between start and end date
     */
    fun getActivityLogsByDateRange(
        start: LocalDateTime,
        end: LocalDateTime,
    ): List<ActivityLogDto> {
        logger.debug { "Getting activity logs between $start and $end" }
        return activityLogRepository
            .findByTimestampBetweenOrderByTimestampDesc(start, end)
            .map { ActivityLogDto.fromEntity(it) }
    }

    /**
     * Get activity logs by status
     */
    fun getActivityLogsByStatus(status: String): List<ActivityLogDto> {
        logger.debug { "Getting activity logs with status $status" }
        return activityLogRepository
            .findByStatus(status)
            .map { ActivityLogDto.fromEntity(it) }
    }
}
