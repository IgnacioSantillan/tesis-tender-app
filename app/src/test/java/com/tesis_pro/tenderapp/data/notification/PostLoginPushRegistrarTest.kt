package com.tesis_pro.tenderapp.data.notification

import com.tesis_pro.tenderapp.data.remote.RegisterRemoteNotificationDevice
import com.tesis_pro.tenderapp.data.remote.RemoteNotificationDataSource
import com.tesis_pro.tenderapp.data.remote.TenderNotificationApi
import com.tesis_pro.tenderapp.data.remote.dto.DeviceRegistrationResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.RegisterDeviceRequestDto
import com.tesis_pro.tenderapp.domain.model.AuthSession
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PostLoginPushRegistrarTest {
    @Test
    fun registerAfterLogin_obtainsTokenAndRegistersDeviceWithBackend() = runBlocking {
        val api = FakePushRegistrationApi()
        val registrar = AndroidPostLoginPushRegistrar(
            tokenProvider = FakePushTokenProvider(Result.success("fcm-token-1")),
            dataSource = RemoteNotificationDataSource(api),
            notificationOptInProvider = { true },
            appVersionProvider = { "1.0" },
        )

        val result = registrar.registerAfterLogin(sampleSession())

        assertEquals(PushRegistrationResult.Registered, result)
        assertEquals("Bearer session-token", api.lastAuthorization)
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
    fun registerAfterLogin_sendsCurrentNotificationOptIn() = runBlocking {
        val api = FakePushRegistrationApi()
        val registrar = AndroidPostLoginPushRegistrar(
            tokenProvider = FakePushTokenProvider(Result.success("fcm-token-1")),
            dataSource = RemoteNotificationDataSource(api),
            notificationOptInProvider = { false },
            appVersionProvider = { "1.0" },
        )

        val result = registrar.registerAfterLogin(sampleSession())

        assertEquals(PushRegistrationResult.Registered, result)
        assertEquals(false, api.lastRequest?.notificationOptIn)
    }

    @Test
    fun registerAfterLogin_returnsFailedWhenTokenCannotBeObtained() = runBlocking {
        val registrar = AndroidPostLoginPushRegistrar(
            tokenProvider = FakePushTokenProvider(Result.failure(IllegalStateException("missing"))),
            dataSource = RemoteNotificationDataSource(FakePushRegistrationApi()),
        )

        val result = registrar.registerAfterLogin(sampleSession())

        assertTrue(result is PushRegistrationResult.Failed)
    }
}

private class FakePushTokenProvider(
    private val result: Result<String>,
) : PushTokenProvider {
    override suspend fun currentToken(): Result<String> = result
}

private class FakePushRegistrationApi : TenderNotificationApi {
    var lastAuthorization: String? = null
    var lastRequest: RegisterDeviceRequestDto? = null

    override suspend fun registerDevice(
        authorization: String,
        request: RegisterDeviceRequestDto,
    ): DeviceRegistrationResponseDto {
        lastAuthorization = authorization
        lastRequest = request
        return DeviceRegistrationResponseDto(
            id = "device-1",
            platform = request.platform,
            pushProvider = request.pushProvider,
            status = "ENABLED",
            registeredAt = "2026-07-10T12:00:00.000Z",
            appVersion = request.appVersion,
        )
    }
}

private fun sampleSession(): AuthSession {
    return AuthSession(
        email = "user@example.com",
        accessToken = "session-token",
        emailVerified = true,
    )
}
