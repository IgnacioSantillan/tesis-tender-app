package com.tesis_pro.tenderapp.domain.notification

data class NotificationScheduleRequest(
    val id: String,
    val loadId: String,
    val kind: NotificationKind,
    val titleKey: NotificationContentKey,
    val messageKey: NotificationContentKey,
    val scheduledAtEpochMillis: Long,
    val createdAtEpochMillis: Long,
)

enum class NotificationContentKey {
    IDEAL_HANGING_TITLE,
    IDEAL_HANGING_MESSAGE,
    DRYING_COMPLETE_TITLE,
    DRYING_COMPLETE_MESSAGE,
    RAIN_RISK_TITLE,
    RAIN_RISK_MESSAGE,
}
