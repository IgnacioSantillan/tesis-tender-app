package com.tesis_pro.tenderapp.domain.notification

interface NotificationPreferencesRepository {
    fun currentPreferences(): NotificationPreferences

    fun updatePreferences(preferences: NotificationPreferences)
}
