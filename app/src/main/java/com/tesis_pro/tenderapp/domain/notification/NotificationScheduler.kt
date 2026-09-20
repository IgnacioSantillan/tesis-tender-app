package com.tesis_pro.tenderapp.domain.notification

interface NotificationScheduler {
    fun schedule(request: NotificationScheduleRequest): NotificationScheduleResult

    fun cancel(notificationId: String)
}
