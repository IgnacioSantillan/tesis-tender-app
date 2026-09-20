package com.tesis_pro.tenderapp.data.notification

import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationPermissionPolicyTest {
    @Test
    fun `notification permission is not required before Android 13`() {
        val result = NotificationPermissionPolicy.resolve(
            sdkInt = Build.VERSION_CODES.S,
            isPermissionGranted = false,
        )

        assertEquals(NotificationRuntimePermissionState.NOT_REQUIRED, result)
    }

    @Test
    fun `notification permission is granted when Android 13 permission is already granted`() {
        val result = NotificationPermissionPolicy.resolve(
            sdkInt = Build.VERSION_CODES.TIRAMISU,
            isPermissionGranted = true,
        )

        assertEquals(NotificationRuntimePermissionState.GRANTED, result)
    }

    @Test
    fun `notification permission should be requested when Android 13 permission is missing`() {
        val result = NotificationPermissionPolicy.resolve(
            sdkInt = Build.VERSION_CODES.TIRAMISU,
            isPermissionGranted = false,
        )

        assertEquals(NotificationRuntimePermissionState.SHOULD_REQUEST, result)
    }
}
