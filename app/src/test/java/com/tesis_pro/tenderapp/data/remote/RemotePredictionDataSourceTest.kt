package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.CreateCompletionPlanRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.CreateDryingPredictionRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.DryingPredictionResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.EstimatedWashingCostResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.LaundryCompletionPlanResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.ProgramCompletionOptionResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.WeatherLocationDto
import com.tesis_pro.tenderapp.data.remote.dto.WeatherSnapshotResponseDto
import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.DryingMethod
import com.tesis_pro.tenderapp.domain.model.DryingVerdict
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostConfidence
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostLevel
import com.tesis_pro.tenderapp.domain.model.LoadSize
import com.tesis_pro.tenderapp.domain.model.SpinSpeedRpm
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.domain.model.WeatherDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemotePredictionDataSourceTest {
    @Test
    fun calculateDryingPrediction_sendsBackendRequestAndMapsResponse() = runBlocking {
        val api = FakePredictionApi()
        val dataSource = RemotePredictionDataSource(api)

        val result = dataSource.calculateDryingPrediction(
            accessToken = "token-1",
            request = CreateRemoteDryingPrediction(
                laundryLoadId = "load-1",
                clothingType = ClothingType.MIXED,
                washingProgram = WashingProgram.NORMAL,
                dryingMethod = DryingMethod.OUTDOOR,
                locationId = "home",
                dryingLocation = DryingLocation.PATIO,
                spinRpm = SpinSpeedRpm.RPM_1400,
                loadSize = LoadSize.LARGE,
                washerEnergyLabel = "A++",
                washerCapacityKg = 8.0,
                waterUsageLiters = 42.0,
            ),
        )

        assertTrue(result is RemoteDataResult.Success)
        val prediction = (result as RemoteDataResult.Success).data
        assertEquals("Bearer token-1", api.lastAuthorization)
        assertEquals(
            CreateDryingPredictionRequestDto(
                laundryLoadId = "load-1",
                clothingType = "MIXED",
                washingProgram = "NORMAL",
                dryingMethod = "OUTDOOR",
                locationId = "home",
                dryingLocationId = "PATIO",
                spinRpm = 1400,
                loadSize = "LARGE",
                washerEnergyLabel = "A++",
                washerCapacityKg = 8.0,
                waterUsageLiters = 42.0,
            ),
            api.lastRequest,
        )
        assertEquals(DryingVerdict.GOOD, prediction.verdict)
        assertEquals(165, prediction.estimatedDryingMinutes)
        assertEquals(86, prediction.suitabilityScore)
        assertEquals(EstimatedWashingCostLevel.LOW, prediction.estimatedCost?.level)
        assertEquals(EstimatedWashingCostConfidence.MEDIUM, prediction.estimatedCost?.confidence)
        assertEquals(0.72, prediction.estimatedCost?.estimatedEnergyKwh ?: 0.0, 0.001)
        assertEquals(24.0, prediction.weatherSnapshot.temperatureCelsius, 0.001)
    }

    @Test
    fun calculateCompletionPlan_sendsUtcRequestAndMapsProgramOptions() = runBlocking {
        val api = FakePredictionApi()
        val dataSource = RemotePredictionDataSource(api)

        val result = dataSource.calculateCompletionPlan(
            accessToken = "token-2",
            request = CreateRemoteCompletionPlan(
                laundryLoadId = "load-2",
                clothingType = ClothingType.MIXED,
                dryingMethod = DryingMethod.OUTDOOR,
                locationId = "home",
                dryingLocation = DryingLocation.PATIO,
                spinRpm = SpinSpeedRpm.RPM_1200,
                loadSize = LoadSize.MEDIUM,
                washerEnergyLabel = "A+++",
                washerCapacityKg = 7.5,
                waterUsageLiters = 39.0,
                plannedStartAtEpochMillis = 1_753_488_000_000L,
                targetReadyAtEpochMillis = 1_753_493_400_000L,
            ),
        )

        assertTrue(result is RemoteDataResult.Success)
        val plan = (result as RemoteDataResult.Success).data
        assertEquals("Bearer token-2", api.lastAuthorization)
        assertEquals(
            CreateCompletionPlanRequestDto(
                laundryLoadId = "load-2",
                clothingType = "MIXED",
                dryingMethod = "OUTDOOR",
                locationId = "home",
                dryingLocationId = "PATIO",
                spinRpm = 1200,
                loadSize = "MEDIUM",
                washerEnergyLabel = "A+++",
                washerCapacityKg = 7.5,
                waterUsageLiters = 39.0,
                plannedStartAt = "2025-07-26T00:00:00.000Z",
                targetReadyAt = "2025-07-26T01:30:00.000Z",
            ),
            api.lastCompletionRequest,
        )
        assertEquals(1_753_487_700_000L, plan.generatedAtEpochMillis)
        assertEquals(1_753_488_000_000L, plan.plannedStartAtEpochMillis)
        assertEquals(1_753_493_400_000L, plan.targetReadyAtEpochMillis)
        assertEquals(WeatherDataSource.OPEN_METEO, plan.weatherSource)
        assertEquals(1_753_509_600_000L, plan.forecastCoverageEndsAtEpochMillis)
        assertEquals(listOf(WashingProgram.QUICK), plan.recommendedPrograms)
        assertEquals(WashingProgram.QUICK, plan.options.single().program)
        assertEquals(1_753_489_800_000L, plan.options.single().washingEndsAtEpochMillis)
        assertEquals(1_753_489_800_000L, plan.options.single().dryingStartsAtEpochMillis)
        assertEquals(1_753_493_400_000L, plan.options.single().estimatedReadyAtEpochMillis)
        assertEquals(0, plan.options.single().marginMinutes)
        assertEquals(DryingVerdict.GOOD, plan.options.single().verdict)
        assertEquals(86, plan.options.single().suitabilityScore)
    }
}

private class FakePredictionApi : TenderPredictionApi {
    var lastAuthorization: String? = null
    var lastRequest: CreateDryingPredictionRequestDto? = null
    var lastCompletionRequest: CreateCompletionPlanRequestDto? = null

    override suspend fun calculateDryingPrediction(
        authorization: String,
        request: CreateDryingPredictionRequestDto,
    ): DryingPredictionResponseDto {
        lastAuthorization = authorization
        lastRequest = request
        return samplePredictionResponse()
    }

    override suspend fun calculateCompletionPlan(
        authorization: String,
        request: CreateCompletionPlanRequestDto,
    ): LaundryCompletionPlanResponseDto {
        lastAuthorization = authorization
        lastCompletionRequest = request
        return sampleCompletionPlanResponse()
    }
}

private fun samplePredictionResponse(): DryingPredictionResponseDto {
    return DryingPredictionResponseDto(
        verdict = "GOOD",
        dryingMethod = "OUTDOOR",
        dryingLocationId = "PATIO",
        dryingLocationLabel = "Patio",
        estimatedDryingMinutes = 165,
        recommendedHangAt = "2026-07-05T14:00:00Z",
        estimatedPickupAt = "2026-07-05T16:45:00Z",
        suitabilityScore = 86,
        reason = "Weather is favorable for drying in Patio.",
        estimatedCost = EstimatedWashingCostResponseDto(
            amount = null,
            currency = null,
            level = "LOW",
            confidence = "MEDIUM",
            estimatedEnergyKwh = 0.72,
            estimatedWaterLiters = 42.0,
        ),
        weatherSnapshot = WeatherSnapshotResponseDto(
            location = WeatherLocationDto(
                id = "home",
                label = "Home patio",
                latitude = -34.6037,
                longitude = -58.3816,
            ),
            capturedAt = "2026-07-05T14:00:00Z",
            forecastFor = "2026-07-05T14:00:00Z",
            condition = "CLEAR",
            temperatureCelsius = 24.0,
            humidityPercent = 48,
            windSpeedKph = 18.0,
            rainProbabilityPercent = 8,
            cloudCoverPercent = 20,
            isStale = false,
        ),
    )
}

private fun sampleCompletionPlanResponse(): LaundryCompletionPlanResponseDto {
    return LaundryCompletionPlanResponseDto(
        generatedAt = "2025-07-25T23:55:00Z",
        plannedStartAt = "2025-07-26T00:00:00Z",
        targetReadyAt = "2025-07-26T01:30:00Z",
        weatherSource = "OPEN_METEO",
        isStale = false,
        forecastCoverageEndsAt = "2025-07-26T06:00:00Z",
        recommendedPrograms = listOf("QUICK"),
        options = listOf(
            ProgramCompletionOptionResponseDto(
                program = "QUICK",
                washingMinutes = 30,
                washingEndsAt = "2025-07-26T00:30:00Z",
                dryingStartsAt = "2025-07-26T00:30:00Z",
                estimatedDryingMinutes = 60,
                estimatedReadyAt = "2025-07-26T01:30:00Z",
                totalElapsedMinutes = 90,
                marginMinutes = 0,
                feasible = true,
                usesForecastExtrapolation = false,
                verdict = "GOOD",
                suitabilityScore = 86,
            ),
        ),
    )
}
