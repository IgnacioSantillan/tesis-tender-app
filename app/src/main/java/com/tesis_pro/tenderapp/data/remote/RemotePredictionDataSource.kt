package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.CreateCompletionPlanRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.CreateDryingPredictionRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.DryingHourlySlotResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.DryingPredictionResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.EstimatedWashingCostResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.LaundryCompletionPlanResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.ProgramCompletionOptionResponseDto
import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.DryingMethod
import com.tesis_pro.tenderapp.domain.model.DryingHourlySlot
import com.tesis_pro.tenderapp.domain.model.DryingPrediction
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCost
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostConfidence
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostLevel
import com.tesis_pro.tenderapp.domain.model.LoadSize
import com.tesis_pro.tenderapp.domain.model.LaundryCompletionPlan
import com.tesis_pro.tenderapp.domain.model.ProgramCompletionOption
import com.tesis_pro.tenderapp.domain.model.SpinSpeedRpm
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.domain.model.WeatherDataSource
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import retrofit2.HttpException

class RemotePredictionDataSource(
    private val api: TenderPredictionApi,
) {
    suspend fun calculateDryingPrediction(
        accessToken: String,
        request: CreateRemoteDryingPrediction,
    ): RemoteDataResult<DryingPrediction> {
        return safeRemoteCall {
            api.calculateDryingPrediction(
                authorization = accessToken.toBearerHeader(),
                request = request.toDto(),
            ).toDomain()
        }
    }

    suspend fun calculateCompletionPlan(
        accessToken: String,
        request: CreateRemoteCompletionPlan,
    ): RemoteDataResult<LaundryCompletionPlan> {
        return safeRemoteCall {
            api.calculateCompletionPlan(
                authorization = accessToken.toBearerHeader(),
                request = request.toDto(),
            ).toDomain()
        }
    }

    private suspend fun <T> safeRemoteCall(block: suspend () -> T): RemoteDataResult<T> {
        return try {
            RemoteDataResult.Success(block())
        } catch (exception: MissingPredictionAccessTokenException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Access token is required for protected prediction calls.",
            )
        } catch (exception: IllegalArgumentException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.INVALID_RESPONSE,
                message = "Backend returned invalid prediction data.",
            )
        } catch (exception: ParseException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.INVALID_RESPONSE,
                message = "Backend returned an invalid prediction date.",
            )
        } catch (exception: HttpException) {
            exception.toRemoteError()
        } catch (exception: IOException) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.NETWORK,
                message = "Unable to reach TenderApp backend predictions.",
            )
        } catch (exception: Exception) {
            RemoteDataResult.Error(
                type = RemoteDataErrorType.UNKNOWN,
                message = "Unexpected remote prediction error.",
            )
        }
    }
}

data class CreateRemoteDryingPrediction(
    val laundryLoadId: String,
    val clothingType: ClothingType,
    val washingProgram: WashingProgram,
    val dryingMethod: DryingMethod,
    val locationId: String,
    val dryingLocation: DryingLocation,
    val spinRpm: SpinSpeedRpm? = null,
    val loadSize: LoadSize? = null,
    val washerEnergyLabel: String? = null,
    val washerCapacityKg: Double? = null,
    val waterUsageLiters: Double? = null,
)

data class CreateRemoteCompletionPlan(
    val laundryLoadId: String,
    val clothingType: ClothingType,
    val dryingMethod: DryingMethod,
    val locationId: String,
    val dryingLocation: DryingLocation,
    val spinRpm: SpinSpeedRpm? = null,
    val loadSize: LoadSize? = null,
    val washerEnergyLabel: String? = null,
    val washerCapacityKg: Double? = null,
    val waterUsageLiters: Double? = null,
    val plannedStartAtEpochMillis: Long,
    val targetReadyAtEpochMillis: Long,
) {
    @Suppress("UNUSED_PARAMETER")
    constructor(
        washerId: String?,
        clothingType: ClothingType,
        dryingMethod: DryingMethod,
        locationId: String,
        dryingLocation: DryingLocation,
        spinRpm: SpinSpeedRpm? = null,
        loadSize: LoadSize? = null,
        plannedStartAtEpochMillis: Long,
        targetReadyAtEpochMillis: Long,
    ) : this(
        laundryLoadId = NEW_LOAD_PREVIEW_ID,
        clothingType = clothingType,
        dryingMethod = dryingMethod,
        locationId = locationId,
        dryingLocation = dryingLocation,
        spinRpm = spinRpm,
        loadSize = loadSize,
        plannedStartAtEpochMillis = plannedStartAtEpochMillis,
        targetReadyAtEpochMillis = targetReadyAtEpochMillis,
    )

    private companion object {
        const val NEW_LOAD_PREVIEW_ID = "new-load-preview"
    }
}

private fun CreateRemoteDryingPrediction.toDto(): CreateDryingPredictionRequestDto {
    return CreateDryingPredictionRequestDto(
        laundryLoadId = laundryLoadId,
        clothingType = clothingType.name,
        washingProgram = washingProgram.name,
        dryingMethod = dryingMethod.name,
        locationId = locationId,
        dryingLocationId = dryingLocation.name,
        spinRpm = spinRpm?.rpm,
        loadSize = loadSize?.name,
        washerEnergyLabel = washerEnergyLabel,
        washerCapacityKg = washerCapacityKg,
        waterUsageLiters = waterUsageLiters,
    )
}

private fun CreateRemoteCompletionPlan.toDto(): CreateCompletionPlanRequestDto {
    return CreateCompletionPlanRequestDto(
        laundryLoadId = laundryLoadId,
        clothingType = clothingType.name,
        dryingMethod = dryingMethod.name,
        locationId = locationId,
        dryingLocationId = dryingLocation.name,
        spinRpm = spinRpm?.rpm,
        loadSize = loadSize?.name,
        washerEnergyLabel = washerEnergyLabel,
        washerCapacityKg = washerCapacityKg,
        waterUsageLiters = waterUsageLiters,
        plannedStartAt = plannedStartAtEpochMillis.toIsoUtcTimestamp(),
        targetReadyAt = targetReadyAtEpochMillis.toIsoUtcTimestamp(),
    )
}

private fun DryingPredictionResponseDto.toDomain(): DryingPrediction {
    val weather = weatherSnapshot.toDomain()
    return DryingPrediction(
        verdict = enumValueOf(verdict),
        method = enumValueOf(dryingMethod),
        estimatedDryingMinutes = estimatedDryingMinutes,
        recommendedHangAtEpochMillis = recommendedHangAt.toEpochMillis(),
        recommendedHangWindowStartEpochMillis = (recommendedHangWindowStart ?: recommendedHangAt).toEpochMillis(),
        recommendedHangWindowEndEpochMillis = (recommendedHangWindowEnd ?: recommendedHangAt).toEpochMillis(),
        estimatedPickupAtEpochMillis = estimatedPickupAt.toEpochMillis(),
        suitabilityScore = suitabilityScore.coerceIn(0, 100),
        reason = reason,
        weatherSnapshot = weather,
        estimatedCost = estimatedCost?.toDomain(),
        hourlySlots = hourlySlots.map { it.toDomain() },
    )
}

private fun LaundryCompletionPlanResponseDto.toDomain(): LaundryCompletionPlan {
    return LaundryCompletionPlan(
        generatedAtEpochMillis = generatedAt.toEpochMillis(),
        plannedStartAtEpochMillis = plannedStartAt.toEpochMillis(),
        targetReadyAtEpochMillis = targetReadyAt.toEpochMillis(),
        weatherSource = weatherSource.toWeatherDataSource(),
        isStale = isStale,
        forecastCoverageEndsAtEpochMillis = forecastCoverageEndsAt?.toEpochMillis(),
        recommendedPrograms = recommendedPrograms.map { enumValueOf<WashingProgram>(it) },
        options = options.map { it.toDomain() },
    )
}

private fun ProgramCompletionOptionResponseDto.toDomain(): ProgramCompletionOption {
    return ProgramCompletionOption(
        program = enumValueOf(program),
        washingMinutes = washingMinutes,
        washingEndsAtEpochMillis = washingEndsAt.toEpochMillis(),
        dryingStartsAtEpochMillis = dryingStartsAt.toEpochMillis(),
        estimatedDryingMinutes = estimatedDryingMinutes,
        estimatedReadyAtEpochMillis = estimatedReadyAt.toEpochMillis(),
        totalElapsedMinutes = totalElapsedMinutes,
        marginMinutes = marginMinutes,
        feasible = feasible,
        usesForecastExtrapolation = usesForecastExtrapolation,
        verdict = enumValueOf(verdict),
        suitabilityScore = suitabilityScore.coerceIn(0, 100),
    )
}

private fun DryingHourlySlotResponseDto.toDomain(): DryingHourlySlot {
    return DryingHourlySlot(
        forecastForEpochMillis = forecastFor.toEpochMillis(),
        verdict = enumValueOf(verdict),
        suitabilityScore = suitabilityScore.coerceIn(0, 100),
        temperatureCelsius = temperatureCelsius,
        humidityPercent = humidityPercent.coerceIn(0, 100),
        windSpeedKph = windSpeedKph.coerceAtLeast(0.0),
        rainProbabilityPercent = rainProbabilityPercent.coerceIn(0, 100),
    )
}

private fun EstimatedWashingCostResponseDto.toDomain(): EstimatedWashingCost {
    return EstimatedWashingCost(
        amount = amount,
        currency = currency,
        level = EstimatedWashingCostLevel.fromCode(level),
        confidence = EstimatedWashingCostConfidence.fromCode(confidence),
        estimatedEnergyKwh = estimatedEnergyKwh,
        estimatedWaterLiters = estimatedWaterLiters,
    )
}

private fun Long.toIsoUtcTimestamp(): String {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(this))
}

private fun String.toWeatherDataSource(): WeatherDataSource {
    return runCatching {
        enumValueOf<WeatherDataSource>(uppercase().replace("-", "_"))
    }.getOrDefault(WeatherDataSource.UNKNOWN)
}

private fun String.toBearerHeader(): String {
    val trimmed = trim()
    if (trimmed.isEmpty()) {
        throw MissingPredictionAccessTokenException()
    }
    return "Bearer $trimmed"
}

private fun HttpException.toRemoteError(): RemoteDataResult.Error {
    return when (code()) {
        401, 403 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.AUTHENTICATION,
            message = "Authentication is required to calculate drying predictions.",
        )
        404 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.NOT_FOUND,
            message = "Prediction data was not found.",
        )
        in 500..599 -> RemoteDataResult.Error(
            type = RemoteDataErrorType.SERVER,
            message = "TenderApp backend could not calculate drying prediction.",
        )
        else -> RemoteDataResult.Error(
            type = RemoteDataErrorType.UNKNOWN,
            message = "Backend rejected the prediction request.",
        )
    }
}

private class MissingPredictionAccessTokenException : RuntimeException()
