package io.content.scraper.temporal.activity.impl

import io.content.scraper.enums.ActivityLogAction
import io.content.scraper.enums.ActivityLogStatus
import io.content.scraper.service.ActivityLogService
import io.content.scraper.temporal.activity.ActivityLogActivity
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class ActivityLogActivityImpl(
    private val activityLogService: ActivityLogService,
) : ActivityLogActivity {
    private val logger = KotlinLogging.logger {}

    override fun logActivity(
        action: ActivityLogAction,
        status: ActivityLogStatus,
        message: String,
    ) {
        activityLogService.logActivity(action.name, status.name, message)
        logger.debug { "Logged activity: $action - $status - $message" }
    }

    override fun logWorkflowStarted(
        workflowType: String,
        details: String,
    ) {
        activityLogService.logActivity(
            action = "WORKFLOW_STARTED",
            status = ActivityLogStatus.SUCCESS.name,
            details = details,
        )
        logger.info { "Workflow started: $workflowType" }
    }

    override fun logWorkflowCompleted(
        workflowType: String,
        success: Boolean,
        details: String,
    ) {
        val status = if (success) ActivityLogStatus.SUCCESS else ActivityLogStatus.FAILED
        activityLogService.logActivity(
            action = "WORKFLOW_COMPLETED",
            status = status.name,
            details = details,
        )
        logger.info { "Workflow completed: $workflowType - Success: $success" }
    }
}
