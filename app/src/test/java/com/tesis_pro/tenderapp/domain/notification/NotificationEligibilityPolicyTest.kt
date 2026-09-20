package com.tesis_pro.tenderapp.domain.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NotificationEligibilityPolicyTest {
    private val policy = NotificationEligibilityPolicy()

    @Test
    fun evaluate_allowsActionableNotificationWhenUserOptedIn() {
        val result = policy.evaluate(
            request = request(kind = NotificationKind.RAIN_RISK),
            preferences = preferences(),
        )

        assertNull(result)
    }

    @Test
    fun evaluate_skipsWhenNotificationsAreDisabled() {
        val result = policy.evaluate(
            request = request(kind = NotificationKind.RAIN_RISK),
            preferences = preferences(notificationsEnabled = false),
        )

        assertEquals(NotificationSkipReason.USER_OPTED_OUT, result)
    }

    @Test
    fun evaluate_skipsWhenSpecificKindIsDisabled() {
        val result = policy.evaluate(
            request = request(kind = NotificationKind.RAIN_RISK),
            preferences = preferences(rainRiskEnabled = false),
        )

        assertEquals(NotificationSkipReason.USER_OPTED_OUT_FOR_KIND, result)
    }

    @Test
    fun evaluate_skipsWhenEventIsNotActionable() {
        val result = policy.evaluate(
            request = request(
                kind = NotificationKind.DRYING_COMPLETE,
                scheduledAtEpochMillis = 1_000L,
                createdAtEpochMillis = 1_000L,
            ),
            preferences = preferences(),
        )

        assertEquals(NotificationSkipReason.NOT_ACTIONABLE, result)
    }

    private fun request(
        kind: NotificationKind,
        scheduledAtEpochMillis: Long = 2_000L,
        createdAtEpochMillis: Long = 1_000L,
    ): NotificationScheduleRequest {
        return NotificationScheduleRequest(
            id = "notification-1",
            loadId = "load-1",
            kind = kind,
            titleKey = NotificationContentKey.RAIN_RISK_TITLE,
            messageKey = NotificationContentKey.RAIN_RISK_MESSAGE,
            scheduledAtEpochMillis = scheduledAtEpochMillis,
            createdAtEpochMillis = createdAtEpochMillis,
        )
    }

    private fun preferences(
        notificationsEnabled: Boolean = true,
        rainRiskEnabled: Boolean = true,
    ): NotificationPreferences {
        return NotificationPreferences(
            notificationsEnabled = notificationsEnabled,
            idealHangingTimeEnabled = true,
            dryingCompleteEnabled = true,
            rainRiskEnabled = rainRiskEnabled,
        )
    }
}
