package com.tesis_pro.tenderapp.data.notification

import com.tesis_pro.tenderapp.domain.notification.NotificationEligibilityPolicy
import com.tesis_pro.tenderapp.domain.notification.NotificationPreferences
import com.tesis_pro.tenderapp.domain.notification.NotificationScheduleRequest
import com.tesis_pro.tenderapp.domain.notification.NotificationScheduleResult
import com.tesis_pro.tenderapp.domain.notification.NotificationScheduler

class LocalNotificationScheduler(
    private val preferencesProvider: () -> NotificationPreferences,
    private val eligibilityPolicy: NotificationEligibilityPolicy = NotificationEligibilityPolicy(),
) : NotificationScheduler {
    constructor(
        preferences: NotificationPreferences,
        eligibilityPolicy: NotificationEligibilityPolicy = NotificationEligibilityPolicy(),
    ) : this({ preferences }, eligibilityPolicy)

    private val scheduledRequests = mutableMapOf<String, NotificationScheduleRequest>()

    override fun schedule(request: NotificationScheduleRequest): NotificationScheduleResult {
        val skipReason = eligibilityPolicy.evaluate(
            request = request,
            preferences = preferencesProvider(),
        )

        if (skipReason != null) {
            return NotificationScheduleResult.Skipped(reason = skipReason)
        }

        scheduledRequests[request.id] = request
        return NotificationScheduleResult.Scheduled(request = request)
    }

    override fun cancel(notificationId: String) {
        scheduledRequests.remove(notificationId)
    }

    fun scheduledCount(): Int {
        return scheduledRequests.size
    }
}
