package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.DeviceRegistrationResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.RegisterDeviceRequestDto
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteNotificationDataSourceTest {
    @Test
    fun registerDevice_sendsBackendRequestAndMapsSuccess() = runBlocking {
        val api = FakeNotificationApi()
        val dataSource = RemoteNotificationDataSource(api)

        val result = dataSource.registerDevice(
            accessToken = "token-1",
            request = RegisterRemoteNotificationDevice(
                deviceToken = "fcm-token-1",
                notificationOptIn = true,
                appVersion = "1.0",
            ),
        )

        assertTrue(result is RemoteDataResult.Success)
        assertEquals("Bearer token-1", api.lastAuthorization)
        assertEquals(
            RegisterDeviceRequestDto(
                deviceToken = "fcm-token-1",
                platform = "ANDROID",
                pushProvider = "FCM",
                notificationOptIn = true,
                appVersion = "1.0",
            ),
            api.lastRequest,
        )
    }

    @Test
    fun registerDevice_rejectsBlankAccessTokenBeforeCallingBackend() = runBlocking {
        val api = FakeNotificationApi()
        val dataSource = RemoteNotificationDataSource(api)

        val result = dataSource.registerDevice(
            accessToken = " ",
            request = RegisterRemoteNotificationDevice(
                deviceToken = "fcm-token-1",
                notificationOptIn = true,
                appVersion = "1.0",
            ),
        )

        assertEquals(RemoteDataErrorType.AUTHENTICATION, (result as RemoteDataResult.Error).type)
        assertEquals(null, api.lastAuthorization)
    }

    @Test
    fun registerDevice_mapsNetworkErrors() = runBlocking {
        val dataSource = RemoteNotificationDataSource(
            FakeNotificationApi(error = IOException("offline")),
        )

        val result = dataSource.registerDevice(
            accessToken = "token-1",
            request = RegisterRemoteNotificationDevice(
                deviceToken = "fcm-token-1",
                notificationOptIn = true,
                appVersion = "1.0",
            ),
        )

        assertEquals(RemoteDataErrorType.NETWORK, (result as RemoteDataResult.Error).type)
    }
}

private class FakeNotificationApi(
    private val error: Exception? = null,
) : TenderNotificationApi {
    var lastAuthorization: String? = null
    var lastRequest: RegisterDeviceRequestDto? = null

    override suspend fun registerDevice(
        authorization: String,
        request: RegisterDeviceRequestDto,
    ): DeviceRegistrationResponseDto {
        error?.let { throw it }
        lastAuthorization = authorization
        lastRequest = request
        return DeviceRegistrationResponseDto(
            id = "device-1",
            platform = "ANDROID",
            pushProvider = "FCM",
            status = "ENABLED",
            registeredAt = "2026-07-10T12:00:00.000Z",
            appVersion = request.appVersion,
        )
    }
}
