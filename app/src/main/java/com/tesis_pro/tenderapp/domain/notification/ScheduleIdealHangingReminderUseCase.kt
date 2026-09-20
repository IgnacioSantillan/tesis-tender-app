package com.tesis_pro.tenderapp.domain.notification

import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.WashingProgram

class ScheduleIdealHangingReminderUseCase(
    private val notificationScheduler: NotificationScheduler,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) {
    fun schedule(
        load: LaundryLoad,
        washingProgram: WashingProgram,
    ): NotificationScheduleResult {
        val now = currentTimeMillis()
        val baseTime = load.startedAtEpochMillis ?: load.createdAtEpochMillis
        val scheduledAt = (baseTime + washingProgram.estimatedCycleMillis())
            .coerceAtLeast(now + MIN_ACTIONABLE_DELAY_MILLIS)

        return notificationScheduler.schedule(
            NotificationScheduleRequest(
                id = "${load.id}-ideal-hanging",
                loadId = load.id,
                kind = NotificationKind.IDEAL_HANGING_TIME,
                titleKey = NotificationContentKey.IDEAL_HANGING_TITLE,
                messageKey = NotificationContentKey.IDEAL_HANGING_MESSAGE,
                scheduledAtEpochMillis = scheduledAt,
                createdAtEpochMillis = now,
            )
        )
    }

    private fun WashingProgram.estimatedCycleMillis(): Long {
        val minutes = when (this) {
            WashingProgram.QUICK -> 30
            WashingProgram.NORMAL -> 60
            WashingProgram.ECO -> 90
            WashingProgram.DELICATE -> 45
        }
        return minutes * MILLIS_PER_MINUTE
    }

    private companion object {
        const val MILLIS_PER_MINUTE = 60_000L
        const val MIN_ACTIONABLE_DELAY_MILLIS = 60_000L
    }
}
