package com.tesis_pro.tenderapp.data.notification

import com.tesis_pro.tenderapp.domain.notification.NotificationPreferences
import com.tesis_pro.tenderapp.domain.notification.NotificationPreferencesRepository

object InMemoryNotificationPreferencesRepository : NotificationPreferencesRepository {
    private var preferences = NotificationPreferences(
        notificationsEnabled = true,
        idealHangingTimeEnabled = true,
        dryingCompleteEnabled = true,
        rainRiskEnabled = true,
    )

    override fun currentPreferences(): NotificationPreferences = preferences

    override fun updatePreferences(preferences: NotificationPreferences) {
        this.preferences = preferences
    }
}
