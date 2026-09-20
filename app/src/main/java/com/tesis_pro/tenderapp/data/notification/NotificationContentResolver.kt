package com.tesis_pro.tenderapp.data.notification

import android.content.Context
import androidx.annotation.StringRes
import com.tesis_pro.tenderapp.R
import com.tesis_pro.tenderapp.domain.notification.NotificationContentKey
import com.tesis_pro.tenderapp.domain.notification.NotificationScheduleRequest

data class LocalizedNotificationContent(
    val title: String,
    val message: String,
)

class NotificationContentResolver(
    private val context: Context,
) {
    fun resolve(request: NotificationScheduleRequest): LocalizedNotificationContent {
        return LocalizedNotificationContent(
            title = context.getString(request.titleKey.stringRes()),
            message = context.getString(request.messageKey.stringRes()),
        )
    }
}

@StringRes
private fun NotificationContentKey.stringRes(): Int {
    return when (this) {
        NotificationContentKey.IDEAL_HANGING_TITLE -> R.string.notification_ideal_hanging_title
        NotificationContentKey.IDEAL_HANGING_MESSAGE -> R.string.notification_ideal_hanging_message
        NotificationContentKey.DRYING_COMPLETE_TITLE -> R.string.notification_drying_complete_title
        NotificationContentKey.DRYING_COMPLETE_MESSAGE -> R.string.notification_drying_complete_message
        NotificationContentKey.RAIN_RISK_TITLE -> R.string.notification_rain_risk_title
        NotificationContentKey.RAIN_RISK_MESSAGE -> R.string.notification_rain_risk_message
    }
}
