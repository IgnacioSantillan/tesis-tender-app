package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.DeviceRegistrationResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.RegisterDeviceRequestDto
import java.io.IOException
import retrofit2.HttpException

class RemoteNotificationDataSource(
    private val api: TenderNotificationApi,
) {
    suspend fun registerDevice(
        accessToken: String,
        request: RegisterRemoteNotificationDevice,
    ): RemoteDataResult<DeviceRegistrationResponseDto> {
        return safeRemoteCall {
            api.registerDevice(
                authorization = accessToken.toBearerHeader(),
                request = request.toDto(),
            )
        }
    }

    private suspend fun <T> safeRemoteCall(block: suspend () -> T): RemoteDataResult<T> {
        return try {
            RemoteDataResult.Success(block())
        } catch (exception: MissingNotificationAccessTokenException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Access token is required for protected notification calls.",
            )
        } catch (exception: HttpException) {
            exception.toRemoteError()
        } catch (exception: IOException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.NETWORK,
                message = "Unable to reach TenderApp backend.",
            )
        } catch (exception: Exception) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.UNKNOWN,
                message = "Unexpected notification registration error.",
            )
        }
    }
}

data class RegisterRemoteNotificationDevice(
    val deviceToken: String,
    val notificationOptIn: Boolean,
    val appVersion: String?,
)

private fun RegisterRemoteNotificationDevice.toDto(): RegisterDeviceRequestDto {
    return RegisterDeviceRequestDto(
        deviceToken = deviceToken,
        platform = "ANDROID",
        pushProvider = "FCM",
        notificationOptIn = notificationOptIn,
        appVersion = appVersion,
    )
}

private fun String.toBearerHeader(): String {
    val trimmed = trim()
    if (trimmed.isEmpty()) {
        throw MissingNotificationAccessTokenException()
    }
    return "Bearer $trimmed"
}

private fun HttpException.toRemoteError(): RemoteDataResult.Error {
    return when (code()) {
        401, 403 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.AUTHENTICATION,
            message = "Authentication is required to register notification device.",
        )
        in 500..599 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.SERVER,
            message = "TenderApp backend could not register notification device.",
        )
        else -> RemoteDataResult.Error(
            type = RemoteDataErrorType.UNKNOWN,
            message = "Backend rejected the notification registration request.",
        )
    }
}

private class MissingNotificationAccessTokenException : RuntimeException()
