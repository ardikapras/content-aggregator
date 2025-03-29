package io.content.scraper.api.controller

import io.content.scraper.api.dto.ActivityLogDto
import io.content.scraper.api.dto.ApiResponse
import io.content.scraper.service.ActivityLogService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

private const val SUCCESSFULLY_RETRIEVED_ACTIVITY_LOGS = "Successfully retrieved activity logs"

@RestController
@RequestMapping("/api/activities")
class ActivityController(
    private val activityLogService: ActivityLogService,
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping
    fun getRecentActivities(): ResponseEntity<ApiResponse<List<ActivityLogDto>>> {
        try {
            val activities = activityLogService.getRecentActivityLogs()
            return ApiResponse.success(activities, SUCCESSFULLY_RETRIEVED_ACTIVITY_LOGS)
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving activity logs: ${e.message}" }
            return ApiResponse.error("Failed to retrieve activity logs: ${e.message}")
        }
    }

    @GetMapping("/date-range")
    fun getActivitiesByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate,
    ): ResponseEntity<ApiResponse<List<ActivityLogDto>>> {
        try {
            val startDateTime = LocalDateTime.of(startDate, LocalTime.MIN)
            val endDateTime = LocalDateTime.of(endDate, LocalTime.MAX)

            val activities = activityLogService.getActivityLogsByDateRange(startDateTime, endDateTime)
            return ApiResponse.success(activities, SUCCESSFULLY_RETRIEVED_ACTIVITY_LOGS)
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving activity logs by date range: ${e.message}" }
            return ApiResponse.error("Failed to retrieve activity logs: ${e.message}")
        }
    }

    @GetMapping("/status")
    fun getActivitiesByStatus(
        @RequestParam status: String,
    ): ResponseEntity<ApiResponse<List<ActivityLogDto>>> {
        try {
            val activities = activityLogService.getActivityLogsByStatus(status)
            return ApiResponse.success(activities, SUCCESSFULLY_RETRIEVED_ACTIVITY_LOGS)
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving activity logs by status: ${e.message}" }
            return ApiResponse.error("Failed to retrieve activity logs: ${e.message}")
        }
    }
}
