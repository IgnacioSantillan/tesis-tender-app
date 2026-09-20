package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.CreateLaundryLoadRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.LaundryLoadResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.UpdateLaundryLoadStatusRequestDto
import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCost
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostConfidence
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostLevel
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.model.LoadSize
import com.tesis_pro.tenderapp.domain.model.SpinSpeedRpm
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import retrofit2.HttpException

class RemoteLaundryDataSource(
    private val api: TenderBackendApi,
) {
    suspend fun listLaundryLoads(accessToken: String): RemoteDataResult<List<LaundryLoad>> {
        return safeRemoteCall {
            api.listLaundryLoads(accessToken.toBearerHeader()).map { it.toDomain() }
        }
    }

    suspend fun createLaundryLoad(
        accessToken: String,
        request: CreateRemoteLaundryLoad,
    ): RemoteDataResult<LaundryLoad> {
        return safeRemoteCall {
            api.createLaundryLoad(
                authorization = accessToken.toBearerHeader(),
                request = request.toDto(),
            ).toDomain()
        }
    }

    suspend fun updateLaundryLoadStatus(
        accessToken: String,
        loadId: String,
        status: LaundryLoadStatus,
    ): RemoteDataResult<LaundryLoad> {
        return safeRemoteCall {
            api.updateLaundryLoadStatus(
                authorization = accessToken.toBearerHeader(),
                loadId = loadId,
                request = UpdateLaundryLoadStatusRequestDto(status = status.name),
            ).toDomain()
        }
    }

    private suspend fun <T> safeRemoteCall(block: suspend () -> T): RemoteDataResult<T> {
        return try {
            RemoteDataResult.Success(block())
        } catch (exception: MissingAccessTokenException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Access token is required for protected backend calls.",
            )
        } catch (exception: IllegalArgumentException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.INVALID_RESPONSE,
                message = "Backend returned invalid laundry load data.",
            )
        } catch (exception: ParseException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.INVALID_RESPONSE,
                message = "Backend returned an invalid laundry load date.",
            )
        } catch (exception: HttpException) {
            val errorBody = exception.response()?.errorBody()?.string()
            logRemoteLaundryError("HTTP ${exception.code()} ${exception.message()} body=$errorBody")
            exception.toRemoteError()
        } catch (exception: IOException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.NETWORK,
                message = "Unable to reach TenderApp backend.",
            )
        } catch (exception: Exception) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.UNKNOWN,
                message = "Unexpected remote data error.",
            )
        }
    }
}

data class CreateRemoteLaundryLoad(
    val washerId: String?,
    val clothingType: ClothingType,
    val washingProgram: WashingProgram,
    val locationId: String,
    val dryingLocation: DryingLocation,
    val spinRpm: SpinSpeedRpm? = null,
    val loadSize: LoadSize? = null,
)

private fun CreateRemoteLaundryLoad.toDto(): CreateLaundryLoadRequestDto {
    return CreateLaundryLoadRequestDto(
        washerId = washerId,
        clothingType = clothingType.name,
        washingProgram = washingProgram.name,
        locationId = locationId,
        dryingLocationId = dryingLocation.name,
        spinRpm = spinRpm?.rpm,
        loadSize = loadSize?.name,
    )
}

private fun LaundryLoadResponseDto.toDomain(): LaundryLoad {
    return LaundryLoad(
        id = id,
        washerId = washerId,
        clothingType = enumValueOf(clothingType.uppercase()),
        washingProgram = enumValueOf(washingProgram.uppercase()),
        status = enumValueOf(status.uppercase()),
        location = WeatherLocation(
            id = locationId,
            label = locationId,
            latitude = null,
            longitude = null,
        ),
        dryingLocation = dryingLocationId.toDryingLocation(),
        createdAtEpochMillis = createdAt.toLaundryEpochMillis(),
        startedAtEpochMillis = startedAt?.toLaundryEpochMillis(),
        completedAtEpochMillis = completedAt?.toLaundryEpochMillis(),
        prediction = null,
        dryingStartedAtEpochMillis = dryingStartedAt?.toLaundryEpochMillis(),
        dryingEstimatedMinutesAtStart = dryingEstimatedMinutesAtStart,
        dryingEstimatedPickupAtEpochMillis = dryingEstimatedPickupAt?.toLaundryEpochMillis(),
        spinRpm = SpinSpeedRpm.fromRpm(spinRpm),
        loadSize = LoadSize.fromCode(loadSize),
        estimatedWashingCost = toEstimatedWashingCost(),
    )
}

private fun LaundryLoadResponseDto.toEstimatedWashingCost(): EstimatedWashingCost? {
    val hasCostData = estimatedWashingEnergyKwh != null ||
        estimatedWashingWaterLiters != null ||
        estimatedWashingCostAmount != null ||
        estimatedWashingCostCurrency != null ||
        estimatedWashingCostLevel != null ||
        estimatedWashingCostConfidence != null
    if (!hasCostData) return null

    return EstimatedWashingCost(
        amount = estimatedWashingCostAmount,
        currency = estimatedWashingCostCurrency,
        level = EstimatedWashingCostLevel.fromCode(estimatedWashingCostLevel),
        confidence = EstimatedWashingCostConfidence.fromCode(estimatedWashingCostConfidence),
        estimatedEnergyKwh = estimatedWashingEnergyKwh,
        estimatedWaterLiters = estimatedWashingWaterLiters,
    )
}

private fun String.toBearerHeader(): String {
    val trimmed = trim()
    if (trimmed.isEmpty()) {
        throw MissingAccessTokenException()
    }
    return "Bearer $trimmed"
}

private fun HttpException.toRemoteError(): RemoteDataResult.Error {
    return when (code()) {
        401, 403 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.AUTHENTICATION,
            message = "Authentication is required to load laundry data.",
        )
        404 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.NOT_FOUND,
            message = "Laundry load was not found.",
        )
        in 500..599 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.SERVER,
            message = "TenderApp backend could not process laundry data.",
        )
        else -> RemoteDataResult.Error(
            type = RemoteDataErrorType.UNKNOWN,
            message = "Backend rejected the laundry data request.",
        )
    }
}

private object IsoUtcDateFormat {
    private val formats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
    ).map { pattern ->
        SimpleDateFormat(pattern, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    fun parse(value: String): Long {
        formats.forEach { format ->
            try {
                return format.parse(value)?.time ?: throw ParseException(value, 0)
            } catch (exception: ParseException) {
                // Try the next supported backend timestamp shape.
            }
        }

        throw ParseException(value, 0)
    }
}

private fun String?.toDryingLocation(): DryingLocation {
    return runCatching {
        enumValueOf<DryingLocation>(this?.uppercase().orEmpty())
    }.getOrDefault(DryingLocation.PATIO)
}

private fun String.toLaundryEpochMillis(): Long {
    return IsoUtcDateFormat.parse(this)
}

private fun logRemoteLaundryError(message: String) {
    runCatching {
        android.util.Log.e("RemoteLaundryDataSource", message)
    }
}

private class MissingAccessTokenException : RuntimeException()
