package com.tesis_pro.tenderapp.data.notification

import android.content.Context
import com.tesis_pro.tenderapp.domain.notification.NotificationPreferences
import com.tesis_pro.tenderapp.domain.notification.NotificationPreferencesRepository

class SharedPreferencesNotificationPreferencesRepository(
    context: Context,
) : NotificationPreferencesRepository {
    private val preferences = context.applicationContext.getSharedPreferences(
        PreferencesName,
        Context.MODE_PRIVATE,
    )

    override fun currentPreferences(): NotificationPreferences {
        return NotificationPreferences(
            notificationsEnabled = preferences.getBoolean(
                KeyNotificationsEnabled,
                DefaultPreferences.notificationsEnabled,
            ),
            idealHangingTimeEnabled = preferences.getBoolean(
                KeyIdealHangingTimeEnabled,
                DefaultPreferences.idealHangingTimeEnabled,
            ),
            dryingCompleteEnabled = preferences.getBoolean(
                KeyDryingCompleteEnabled,
                DefaultPreferences.dryingCompleteEnabled,
            ),
            rainRiskEnabled = preferences.getBoolean(
                KeyRainRiskEnabled,
                DefaultPreferences.rainRiskEnabled,
            ),
            quietHoursEnabled = preferences.getBoolean(
                KeyQuietHoursEnabled,
                DefaultPreferences.quietHoursEnabled,
            ),
            quietHoursStart = preferences.getString(
                KeyQuietHoursStart,
                DefaultPreferences.quietHoursStart,
            ).orEmpty().ifBlank { DefaultPreferences.quietHoursStart },
            quietHoursEnd = preferences.getString(
                KeyQuietHoursEnd,
                DefaultPreferences.quietHoursEnd,
            ).orEmpty().ifBlank { DefaultPreferences.quietHoursEnd },
        )
    }

    override fun updatePreferences(preferences: NotificationPreferences) {
        this.preferences.edit()
            .putBoolean(KeyNotificationsEnabled, preferences.notificationsEnabled)
            .putBoolean(KeyIdealHangingTimeEnabled, preferences.idealHangingTimeEnabled)
            .putBoolean(KeyDryingCompleteEnabled, preferences.dryingCompleteEnabled)
            .putBoolean(KeyRainRiskEnabled, preferences.rainRiskEnabled)
            .putBoolean(KeyQuietHoursEnabled, preferences.quietHoursEnabled)
            .putString(KeyQuietHoursStart, preferences.quietHoursStart)
            .putString(KeyQuietHoursEnd, preferences.quietHoursEnd)
            .apply()
    }

    companion object {
        private const val PreferencesName = "notification_preferences"
        private const val KeyNotificationsEnabled = "notifications_enabled"
        private const val KeyIdealHangingTimeEnabled = "ideal_hanging_time_enabled"
        private const val KeyDryingCompleteEnabled = "drying_complete_enabled"
        private const val KeyRainRiskEnabled = "rain_risk_enabled"
        private const val KeyQuietHoursEnabled = "quiet_hours_enabled"
        private const val KeyQuietHoursStart = "quiet_hours_start"
        private const val KeyQuietHoursEnd = "quiet_hours_end"

        val DefaultPreferences = NotificationPreferences(
            notificationsEnabled = true,
            idealHangingTimeEnabled = true,
            dryingCompleteEnabled = true,
            rainRiskEnabled = true,
        )
    }
}
