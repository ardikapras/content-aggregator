package io.content.scraper.api.dto

import io.content.scraper.models.ActivityLog

/**
 * Data Transfer Object for ActivityLog
 */
data class ActivityLogDto(
    val id: String,
    val timestamp: String,
    val action: String,
    val status: String,
    val details: String?,
) {
    companion object {
        fun fromEntity(entity: ActivityLog): ActivityLogDto =
            ActivityLogDto(
                id = entity.id.toString(),
                timestamp = entity.timestamp.toString(),
                action = entity.action,
                status = entity.status,
                details = entity.details,
            )
    }
}
