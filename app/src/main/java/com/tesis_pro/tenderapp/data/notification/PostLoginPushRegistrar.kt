package com.tesis_pro.tenderapp.data.notification

import com.tesis_pro.tenderapp.BuildConfig
import com.tesis_pro.tenderapp.data.remote.BackendNotificationApiClient
import com.tesis_pro.tenderapp.data.remote.RegisterRemoteNotificationDevice
import com.tesis_pro.tenderapp.data.remote.RemoteDataResult
import com.tesis_pro.tenderapp.data.remote.RemoteNotificationDataSource
import com.tesis_pro.tenderapp.domain.model.AuthSession

interface PostLoginPushRegistrar {
    suspend fun registerAfterLogin(session: AuthSession): PushRegistrationResult
}

sealed interface PushRegistrationResult {
    data object Registered : PushRegistrationResult
    data class Skipped(val reason: String) : PushRegistrationResult
    data class Failed(val reason: String) : PushRegistrationResult
}

object NoOpPostLoginPushRegistrar : PostLoginPushRegistrar {
    override suspend fun registerAfterLogin(session: AuthSession): PushRegistrationResult {
        return PushRegistrationResult.Skipped("Push registration is not configured.")
    }
}

class AndroidPostLoginPushRegistrar(
    private val tokenProvider: PushTokenProvider,
    private val dataSource: RemoteNotificationDataSource = RemoteNotificationDataSource(
        BackendNotificationApiClient.create(),
    ),
    private val notificationOptInProvider: () -> Boolean = { true },
    private val appVersionProvider: () -> String = { BuildConfig.VERSION_NAME },
) : PostLoginPushRegistrar {
    override suspend fun registerAfterLogin(session: AuthSession): PushRegistrationResult {
        val token = tokenProvider.currentToken().getOrElse {
            return PushRegistrationResult.Failed("Unable to obtain Firebase Messaging token.")
        }

        return when (
            dataSource.registerDevice(
                accessToken = session.accessToken,
                request = RegisterRemoteNotificationDevice(
                    deviceToken = token,
                    notificationOptIn = notificationOptInProvider(),
                    appVersion = appVersionProvider(),
                ),
            )
        ) {
            is RemoteDataResult.Success -> PushRegistrationResult.Registered
            is RemoteDataResult.Error -> PushRegistrationResult.Failed("Backend rejected push registration.")
        }
    }
}
