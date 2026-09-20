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

class ScheduleIdealHangingReminderUseCaseTest {
    @Test
    fun schedule_queuesIdealHangingReminderWhenPreferenceIsEnabled() {
        val useCase = ScheduleIdealHangingReminderUseCase(
            notificationScheduler = LocalNotificationScheduler(preferences()),
            currentTimeMillis = { 1_000L },
        )

        val result = useCase.schedule(
            load = load(createdAtEpochMillis = 1_000L, startedAtEpochMillis = 2_000L),
            washingProgram = WashingProgram.QUICK,
        )

        assertTrue(result is NotificationScheduleResult.Scheduled)
        val request = (result as NotificationScheduleResult.Scheduled).request
        assertEquals("load-1-ideal-hanging", request.id)
        assertEquals(NotificationKind.IDEAL_HANGING_TIME, request.kind)
        assertEquals(NotificationContentKey.IDEAL_HANGING_TITLE, request.titleKey)
        assertEquals(NotificationContentKey.IDEAL_HANGING_MESSAGE, request.messageKey)
        assertEquals(2_000L + 30 * 60_000L, request.scheduledAtEpochMillis)
    }

    @Test
    fun schedule_skipsWhenIdealHangingReminderIsDisabled() {
        val useCase = ScheduleIdealHangingReminderUseCase(
            notificationScheduler = LocalNotificationScheduler(
                preferences(idealHangingTimeEnabled = false),
            ),
            currentTimeMillis = { 1_000L },
        )

        val result = useCase.schedule(
            load = load(),
            washingProgram = WashingProgram.ECO,
        )

        assertEquals(
            NotificationSkipReason.USER_OPTED_OUT_FOR_KIND,
            (result as NotificationScheduleResult.Skipped).reason,
        )
    }

    private fun load(
        createdAtEpochMillis: Long = 1_000L,
        startedAtEpochMillis: Long? = 1_000L,
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
        )
    }

    private fun preferences(
        idealHangingTimeEnabled: Boolean = true,
    ): NotificationPreferences {
        return NotificationPreferences(
            notificationsEnabled = true,
            idealHangingTimeEnabled = idealHangingTimeEnabled,
            dryingCompleteEnabled = true,
            rainRiskEnabled = true,
        )
    }
}
