package com.tesis_pro.tenderapp.domain.notification

import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.WashingProgram

class SchedulePickupReminderUseCase(
    private val notificationScheduler: NotificationScheduler,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) {
    fun schedule(
        load: LaundryLoad,
        washingProgram: WashingProgram,
        clothingType: ClothingType,
    ): NotificationScheduleResult {
        val now = currentTimeMillis()
        val scheduledAt = if (load.dryingStartedAtEpochMillis != null) {
            load.dryingStartedAtEpochMillis + clothingType.estimatedDryingMillis()
        } else {
            val baseTime = load.startedAtEpochMillis ?: load.createdAtEpochMillis
            baseTime + washingProgram.estimatedCycleMillis() + clothingType.estimatedDryingMillis()
        }
        val actionableScheduledAt = scheduledAt
            .coerceAtLeast(now + MIN_ACTIONABLE_DELAY_MILLIS)

        return notificationScheduler.schedule(
            NotificationScheduleRequest(
                id = "${load.id}-pickup",
                loadId = load.id,
                kind = NotificationKind.DRYING_COMPLETE,
                titleKey = NotificationContentKey.DRYING_COMPLETE_TITLE,
                messageKey = NotificationContentKey.DRYING_COMPLETE_MESSAGE,
                scheduledAtEpochMillis = actionableScheduledAt,
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

    private fun ClothingType.estimatedDryingMillis(): Long {
        val minutes = when (this) {
            ClothingType.LIGHT_CLOTHES -> 120
            ClothingType.HEAVY_CLOTHES -> 240
            ClothingType.BEDDING -> 300
            ClothingType.DELICATES -> 150
            ClothingType.MIXED -> 180
        }
        return minutes * MILLIS_PER_MINUTE
    }

    private companion object {
        const val MILLIS_PER_MINUTE = 60_000L
        const val MIN_ACTIONABLE_DELAY_MILLIS = 60_000L
    }
}
