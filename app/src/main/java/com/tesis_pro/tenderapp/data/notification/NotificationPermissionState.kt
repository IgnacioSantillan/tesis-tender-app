package com.tesis_pro.tenderapp.data.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

enum class NotificationRuntimePermissionState {
    NOT_REQUIRED,
    GRANTED,
    SHOULD_REQUEST,
}

object NotificationPermissionPolicy {
    fun resolve(
        sdkInt: Int,
        isPermissionGranted: Boolean,
    ): NotificationRuntimePermissionState {
        return when {
            sdkInt < Build.VERSION_CODES.TIRAMISU -> NotificationRuntimePermissionState.NOT_REQUIRED
            isPermissionGranted -> NotificationRuntimePermissionState.GRANTED
            else -> NotificationRuntimePermissionState.SHOULD_REQUEST
        }
    }
}

class AndroidNotificationPermissionStateReader(
    private val context: Context,
) {
    fun read(): NotificationRuntimePermissionState {
        val isPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        return NotificationPermissionPolicy.resolve(
            sdkInt = Build.VERSION.SDK_INT,
            isPermissionGranted = isPermissionGranted,
        )
    }
}
