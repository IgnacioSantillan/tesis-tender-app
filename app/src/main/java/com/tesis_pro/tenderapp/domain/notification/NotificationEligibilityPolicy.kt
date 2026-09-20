package com.tesis_pro.tenderapp.domain.notification

class NotificationEligibilityPolicy {
    fun evaluate(
        request: NotificationScheduleRequest,
        preferences: NotificationPreferences,
    ): NotificationSkipReason? {
        if (!preferences.notificationsEnabled) {
            return NotificationSkipReason.USER_OPTED_OUT
        }

        if (!request.kind.isEnabledBy(preferences)) {
            return NotificationSkipReason.USER_OPTED_OUT_FOR_KIND
        }

        if (request.scheduledAtEpochMillis <= request.createdAtEpochMillis) {
            return NotificationSkipReason.NOT_ACTIONABLE
        }

        return null
    }

    private fun NotificationKind.isEnabledBy(preferences: NotificationPreferences): Boolean {
        return when (this) {
            NotificationKind.IDEAL_HANGING_TIME -> preferences.idealHangingTimeEnabled
            NotificationKind.DRYING_COMPLETE -> preferences.dryingCompleteEnabled
            NotificationKind.RAIN_RISK -> preferences.rainRiskEnabled
        }
    }
}
