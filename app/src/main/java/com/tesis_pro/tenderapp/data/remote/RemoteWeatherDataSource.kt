package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.domain.model.WeatherSnapshot
import java.io.IOException
import java.text.ParseException
import retrofit2.HttpException

class RemoteWeatherDataSource(
    private val api: TenderBackendApi,
) {
    suspend fun currentWeather(
        accessToken: String,
        locationId: String,
    ): RemoteDataResult<WeatherSnapshot> {
        return safeRemoteCall {
            api.currentWeather(
                authorization = accessToken.toBearerHeader(),
                locationId = locationId,
            ).toDomain()
        }
    }

    private suspend fun <T> safeRemoteCall(block: suspend () -> T): RemoteDataResult<T> {
        return try {
            RemoteDataResult.Success(block())
        } catch (exception: MissingWeatherAccessTokenException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Access token is required for protected weather calls.",
            )
        } catch (exception: IllegalArgumentException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.INVALID_RESPONSE,
                message = "Backend returned invalid weather data.",
            )
        } catch (exception: ParseException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.INVALID_RESPONSE,
                message = "Backend returned an invalid weather date.",
            )
        } catch (exception: HttpException) {
            exception.toRemoteError()
        } catch (exception: IOException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.NETWORK,
                message = "Unable to reach TenderApp backend weather.",
            )
        } catch (exception: Exception) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.UNKNOWN,
                message = "Unexpected remote weather error.",
            )
        }
    }
}

private fun String.toBearerHeader(): String {
    val trimmed = trim()
    if (trimmed.isEmpty()) {
        throw MissingWeatherAccessTokenException()
    }
    return "Bearer $trimmed"
}

private fun HttpException.toRemoteError(): RemoteDataResult.Error {
    return when (code()) {
        401, 403 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.AUTHENTICATION,
            message = "Authentication is required to load weather data.",
        )
        404 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.NOT_FOUND,
            message = "Weather data was not found.",
        )
        in 500..599 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.SERVER,
            message = "TenderApp backend could not process weather data.",
        )
        else -> RemoteDataResult.Error(
            type = RemoteDataErrorType.UNKNOWN,
            message = "Backend rejected the weather request.",
        )
    }
}

private class MissingWeatherAccessTokenException : RuntimeException()
