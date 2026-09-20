package com.tesis_pro.tenderapp.data.notification

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.RemoteMessage
import com.tesis_pro.tenderapp.R
import com.tesis_pro.tenderapp.data.settings.AppAppearancePreferencesStore
import com.tesis_pro.tenderapp.data.settings.AppLanguagePreference
import kotlin.math.absoluteValue
import java.util.Locale

class FcmNotificationRenderer(
    private val context: Context,
) {
    fun show(message: RemoteMessage) {
        if (!canShowNotifications()) return

        LaundryNotificationChannels.ensureCreated(context)

        val resourceContext = context.localizedForNotificationLanguage()
        val title = message.data.notificationTitleKey()?.let(resourceContext::localizedNotificationText)
            ?: message.notification?.title
            ?: message.data["title"]
            ?: resourceContext.getString(R.string.notification_drying_complete_title)
        val body = message.data.notificationBodyKey()?.let(resourceContext::localizedNotificationText)
            ?: message.notification?.body
            ?: message.data["body"]
            ?: resourceContext.getString(R.string.notification_drying_complete_message)
        val notificationId = message.messageId?.hashCode()?.absoluteValue ?: System.currentTimeMillis().toInt()
        val notification = NotificationCompat.Builder(
            context,
            context.getString(R.string.notification_channel_laundry_id),
        )
            .setSmallIcon(R.drawable.ic_new_load)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        context
            .getSystemService(NotificationManager::class.java)
            .notify(notificationId, notification)
    }

    private fun canShowNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}

private fun Map<String, String>.notificationTitleKey(): String? {
    return this["notificationTitleKey"]
        ?: this["titleKey"]
        ?: this["title_key"]
}

private fun Map<String, String>.notificationBodyKey(): String? {
    return this["notificationBodyKey"]
        ?: this["bodyKey"]
        ?: this["body_key"]
}

private fun Context.localizedForNotificationLanguage(): Context {
    val language = AppAppearancePreferencesStore(this).getPreferences().language
    val locale = when (language) {
        AppLanguagePreference.SYSTEM -> return this
        AppLanguagePreference.SPANISH -> Locale("es")
        AppLanguagePreference.ENGLISH -> Locale("en")
    }
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)
    return createConfigurationContext(configuration)
}

private fun Context.localizedNotificationText(key: String): String? {
    val resourceId = key.notificationStringRes() ?: return null
    return getString(resourceId)
}

@StringRes
private fun String.notificationStringRes(): Int? {
    return when (this) {
        "notification_ideal_hanging_title",
        "notification_ideal_hanging_time_title",
        -> R.string.notification_ideal_hanging_title
        "notification_ideal_hanging_message",
        "notification_ideal_hanging_time_message",
        -> R.string.notification_ideal_hanging_message
        "notification_drying_complete_title" -> R.string.notification_drying_complete_title
        "notification_drying_complete_message" -> R.string.notification_drying_complete_message
        "notification_rain_risk_title" -> R.string.notification_rain_risk_title
        "notification_rain_risk_message" -> R.string.notification_rain_risk_message
        else -> null
    }
}
