package com.tesis_pro.tenderapp.domain.notification

data class NotificationPreferences(
    val notificationsEnabled: Boolean,
    val idealHangingTimeEnabled: Boolean,
    val dryingCompleteEnabled: Boolean,
    val rainRiskEnabled: Boolean,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00",
)
