package io.content.scraper.temporal.activity

import io.content.scraper.enums.ActivityLogAction
import io.content.scraper.enums.ActivityLogStatus
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface ActivityLogActivity {
    @ActivityMethod
    fun logActivity(
        action: ActivityLogAction,
        status: ActivityLogStatus,
        message: String,
    )

    @ActivityMethod
    fun logWorkflowStarted(
        workflowType: String,
        details: String,
    )

    @ActivityMethod
    fun logWorkflowCompleted(
        workflowType: String,
        success: Boolean,
        details: String,
    )
}
