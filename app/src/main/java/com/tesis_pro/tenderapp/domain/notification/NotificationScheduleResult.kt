package com.tesis_pro.tenderapp.domain.notification

sealed interface NotificationScheduleResult {
    data class Scheduled(
        val request: NotificationScheduleRequest,
    ) : NotificationScheduleResult

    data class Skipped(
        val reason: NotificationSkipReason,
    ) : NotificationScheduleResult
}
