package com.tesis_pro.tenderapp.domain.notification

import com.tesis_pro.tenderapp.data.notification.LocalNotificationScheduler
import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SchedulePickupReminderUseCaseTest {
    @Test
    fun schedule_queuesPickupReminderWhenPreferenceIsEnabled() {
        val useCase = SchedulePickupReminderUseCase(
            notificationScheduler = LocalNotificationScheduler(preferences()),
            currentTimeMillis = { 1_000L },
        )

        val result = useCase.schedule(
            load = load(createdAtEpochMillis = 1_000L, startedAtEpochMillis = 2_000L),
            washingProgram = WashingProgram.QUICK,
            clothingType = ClothingType.LIGHT_CLOTHES,
        )

        assertTrue(result is NotificationScheduleResult.Scheduled)
        val request = (result as NotificationScheduleResult.Scheduled).request
        assertEquals("load-1-pickup", request.id)
        assertEquals(NotificationKind.DRYING_COMPLETE, request.kind)
        assertEquals(NotificationContentKey.DRYING_COMPLETE_TITLE, request.titleKey)
        assertEquals(NotificationContentKey.DRYING_COMPLETE_MESSAGE, request.messageKey)
        assertEquals(2_000L + 150 * 60_000L, request.scheduledAtEpochMillis)
    }

    @Test
    fun schedule_skipsWhenPickupReminderIsDisabled() {
        val useCase = SchedulePickupReminderUseCase(
            notificationScheduler = LocalNotificationScheduler(
                preferences(dryingCompleteEnabled = false),
            ),
            currentTimeMillis = { 1_000L },
        )

        val result = useCase.schedule(
            load = load(),
            washingProgram = WashingProgram.ECO,
            clothingType = ClothingType.MIXED,
        )

        assertEquals(
            NotificationSkipReason.USER_OPTED_OUT_FOR_KIND,
            (result as NotificationScheduleResult.Skipped).reason,
        )
    }

    @Test
    fun schedule_usesDryingStartWhenLoadIsAlreadyHanging() {
        val useCase = SchedulePickupReminderUseCase(
            notificationScheduler = LocalNotificationScheduler(preferences()),
            currentTimeMillis = { 1_000L },
        )

        val result = useCase.schedule(
            load = load(
                createdAtEpochMillis = 1_000L,
                startedAtEpochMillis = 2_000L,
                dryingStartedAtEpochMillis = 5_000L,
            ),
            washingProgram = WashingProgram.ECO,
            clothingType = ClothingType.LIGHT_CLOTHES,
        )

        assertTrue(result is NotificationScheduleResult.Scheduled)
        val request = (result as NotificationScheduleResult.Scheduled).request
        assertEquals(5_000L + 120 * 60_000L, request.scheduledAtEpochMillis)
    }

    private fun load(
        createdAtEpochMillis: Long = 1_000L,
        startedAtEpochMillis: Long? = 1_000L,
        dryingStartedAtEpochMillis: Long? = null,
    ): LaundryLoad {
        return LaundryLoad(
            id = "load-1",
            washerId = "washer-1",
            clothingType = ClothingType.MIXED,
            washingProgram = WashingProgram.ECO,
            status = LaundryLoadStatus.WASHING,
            location = WeatherLocation(
                id = "home",
                label = "Home",
                latitude = null,
                longitude = null,
            ),
            createdAtEpochMillis = createdAtEpochMillis,
            startedAtEpochMillis = startedAtEpochMillis,
            completedAtEpochMillis = null,
            prediction = null,
            dryingStartedAtEpochMillis = dryingStartedAtEpochMillis,
        )
    }

    private fun preferences(
        dryingCompleteEnabled: Boolean = true,
    ): NotificationPreferences {
        return NotificationPreferences(
            notificationsEnabled = true,
            idealHangingTimeEnabled = true,
            dryingCompleteEnabled = dryingCompleteEnabled,
            rainRiskEnabled = true,
        )
    }
}
