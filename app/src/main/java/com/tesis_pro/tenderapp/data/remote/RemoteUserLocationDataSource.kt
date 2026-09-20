package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.SaveUserLocationRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.UserLocationResponseDto
import java.io.IOException
import retrofit2.HttpException

class RemoteUserLocationDataSource(
    private val api: TenderBackendApi,
) {
    suspend fun saveUserLocation(
        accessToken: String,
        request: SaveUserLocationRequestDto,
    ): RemoteDataResult<UserLocationResponseDto> {
        return safeRemoteCall {
            api.saveUserLocation(
                authorization = accessToken.toBearerHeader(),
                request = request,
            )
        }
    }

    private suspend fun <T> safeRemoteCall(block: suspend () -> T): RemoteDataResult<T> {
        return try {
            RemoteDataResult.Success(block())
        } catch (exception: MissingUserLocationAccessTokenException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Access token is required to save user location.",
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
                message = "Unexpected user location error.",
            )
        }
    }
}

private fun String.toBearerHeader(): String {
    val trimmed = trim()
    if (trimmed.isEmpty()) {
        throw MissingUserLocationAccessTokenException()
    }
    return "Bearer $trimmed"
}

private fun HttpException.toRemoteError(): RemoteDataResult.Error {
    return when (code()) {
        401, 403 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.AUTHENTICATION,
            message = "Authentication is required to save user location.",
        )
        in 500..599 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.SERVER,
            message = "TenderApp backend could not save user location.",
        )
        else -> RemoteDataResult.Error(
            type = RemoteDataErrorType.UNKNOWN,
            message = "Backend rejected the user location request.",
        )
    }
}

private class MissingUserLocationAccessTokenException : RuntimeException()
