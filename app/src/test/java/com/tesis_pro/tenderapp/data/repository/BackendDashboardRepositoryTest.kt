package com.tesis_pro.tenderapp.data.repository

import com.tesis_pro.tenderapp.data.remote.RemoteLaundryDataSource
import com.tesis_pro.tenderapp.data.remote.RemotePredictionDataSource
import com.tesis_pro.tenderapp.data.remote.RemoteWeatherDataSource
import com.tesis_pro.tenderapp.data.remote.TenderBackendApi
import com.tesis_pro.tenderapp.data.remote.TenderPredictionApi
import com.tesis_pro.tenderapp.data.remote.dto.CreateDryingPredictionRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.CreateCompletionPlanRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.CreateLaundryLoadRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.DryingPredictionResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.HealthResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.LaundryLoadResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.LaundryCompletionPlanResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.SaveWasherRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.SaveUserLocationRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.SupabaseHealthResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.UpdateLaundryLoadStatusRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.UserLocationResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.WeatherLocationDto
import com.tesis_pro.tenderapp.data.remote.dto.WeatherSnapshotResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.WasherResponseDto
import com.tesis_pro.tenderapp.domain.repository.DashboardLoadResult
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackendDashboardRepositoryTest {
    @Test
    fun loadDashboard_returnsBackendOnlineWhenHealthWorksAndSessionIsMissing() = runBlocking {
        val api = FakeDashboardApi()
        val repository = BackendDashboardRepository(
            api = api,
            laundryDataSource = RemoteLaundryDataSource(api),
            weatherDataSource = RemoteWeatherDataSource(api),
            householdSettingsRepository = LocalHouseholdSettingsRepository(),
            accessTokenProvider = { null },
        )

        val result = repository.loadDashboard()

        assertEquals(
            DashboardLoadResult.Success(
                activeLoad = null,
                weatherSnapshot = null,
                sourceLabel = "Backend online",
            ),
            result,
        )
        assertEquals(1, api.healthCalls)
        assertEquals(null, api.lastLaundryAuthorization)
    }

    @Test
    fun loadDashboard_returnsActiveLaundryLoadWhenSessionExists() = runBlocking {
        val api = FakeDashboardApi(
            loads = listOf(sampleLoadResponse(status = "COMPLETED"), sampleLoadResponse(status = "DRYING")),
        )
        val predictionApi = FakeDashboardPredictionApi()
        val repository = BackendDashboardRepository(
            api = api,
            laundryDataSource = RemoteLaundryDataSource(api),
            weatherDataSource = RemoteWeatherDataSource(api),
            predictionDataSource = RemotePredictionDataSource(predictionApi),
            householdSettingsRepository = LocalHouseholdSettingsRepository(),
            accessTokenProvider = { "token-1" },
        )

        val result = repository.loadDashboard()

        assertTrue(result is DashboardLoadResult.Success)
        val success = result as DashboardLoadResult.Success
        assertEquals("Backend prediction", success.sourceLabel)
        assertEquals("load-DRYING", success.activeLoad?.id)
        assertEquals(24.0, success.weatherSnapshot?.temperatureCelsius)
        assertEquals(165, success.dryingPrediction?.estimatedDryingMinutes)
        assertEquals("Bearer token-1", api.lastLaundryAuthorization)
        assertEquals("Bearer token-1", api.lastWeatherAuthorization)
        assertEquals("home", api.lastWeatherLocationId)
        assertEquals("Bearer token-1", predictionApi.lastAuthorization)
        assertEquals("load-DRYING", predictionApi.lastRequest?.laundryLoadId)
        assertEquals("PATIO", predictionApi.lastRequest?.dryingLocationId)
    }

    @Test
    fun loadDashboard_mapsHealthNetworkErrorToDashboardError() = runBlocking {
        val repository = BackendDashboardRepository(
            api = FakeDashboardApi(healthError = IOException("offline")),
            laundryDataSource = RemoteLaundryDataSource(FakeDashboardApi()),
            weatherDataSource = RemoteWeatherDataSource(FakeDashboardApi()),
            householdSettingsRepository = LocalHouseholdSettingsRepository(),
            accessTokenProvider = { "token-1" },
        )

        val result = repository.loadDashboard()

        assertEquals(
            DashboardLoadResult.Error("Backend is unreachable. Check connection and API URL."),
            result,
        )
    }
}

private class FakeDashboardPredictionApi : TenderPredictionApi {
    var lastAuthorization: String? = null
    var lastRequest: CreateDryingPredictionRequestDto? = null

    override suspend fun calculateDryingPrediction(
        authorization: String,
        request: CreateDryingPredictionRequestDto,
    ): DryingPredictionResponseDto {
        lastAuthorization = authorization
        lastRequest = request
        return DryingPredictionResponseDto(
            verdict = "GOOD",
            dryingMethod = "OUTDOOR",
            dryingLocationId = request.dryingLocationId ?: "PATIO",
            dryingLocationLabel = "Patio",
            estimatedDryingMinutes = 165,
            recommendedHangAt = "2026-07-05T14:00:00Z",
            estimatedPickupAt = "2026-07-05T16:45:00Z",
            suitabilityScore = 86,
            reason = "Weather is favorable for drying in Patio.",
            weatherSnapshot = sampleWeatherResponse(),
        )
    }

    override suspend fun calculateCompletionPlan(
        authorization: String,
        request: CreateCompletionPlanRequestDto,
    ): LaundryCompletionPlanResponseDto {
        error("Not used by dashboard repository tests")
    }
}

private class FakeDashboardApi(
    private val loads: List<LaundryLoadResponseDto> = emptyList(),
    private val healthError: Exception? = null,
) : TenderBackendApi {
    var healthCalls = 0
    var lastLaundryAuthorization: String? = null
    var lastWeatherAuthorization: String? = null
    var lastWeatherLocationId: String? = null

    override suspend fun health(): HealthResponseDto {
        healthCalls += 1
        healthError?.let { throw it }
        return HealthResponseDto(status = "ok", version = "test")
    }

    override suspend fun supabaseHealth(): SupabaseHealthResponseDto {
        return SupabaseHealthResponseDto(
            status = "ok",
            projectHost = "example.supabase.co",
            checkedAt = "2026-07-07T12:00:00Z",
            message = null,
        )
    }

    override suspend fun listLaundryLoads(authorization: String): List<LaundryLoadResponseDto> {
        lastLaundryAuthorization = authorization
        return loads
    }

    override suspend fun currentWeather(
        authorization: String,
        locationId: String,
    ): WeatherSnapshotResponseDto {
        lastWeatherAuthorization = authorization
        lastWeatherLocationId = locationId
        return sampleWeatherResponse()
    }

    override suspend fun createLaundryLoad(
        authorization: String,
        request: CreateLaundryLoadRequestDto,
    ): LaundryLoadResponseDto {
        error("Not used by dashboard repository tests")
    }

    override suspend fun updateLaundryLoadStatus(
        authorization: String,
        loadId: String,
        request: UpdateLaundryLoadStatusRequestDto,
    ): LaundryLoadResponseDto {
        error("Not used by dashboard repository tests")
    }

    override suspend fun listWashers(authorization: String): List<WasherResponseDto> {
        error("Not used by dashboard repository tests")
    }

    override suspend fun createWasher(
        authorization: String,
        request: SaveWasherRequestDto,
    ): WasherResponseDto {
        error("Not used by dashboard repository tests")
    }

    override suspend fun updateWasher(
        authorization: String,
        washerId: String,
        request: SaveWasherRequestDto,
    ): WasherResponseDto {
        error("Not used by dashboard repository tests")
    }

    override suspend fun retireWasher(
        authorization: String,
        washerId: String,
    ) {
        error("Not used by dashboard repository tests")
    }

    override suspend fun saveUserLocation(
        authorization: String,
        request: SaveUserLocationRequestDto,
    ): UserLocationResponseDto {
        error("Not used by dashboard repository tests")
    }
}

private fun sampleWeatherResponse(): WeatherSnapshotResponseDto {
    return WeatherSnapshotResponseDto(
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
    )
}

private fun sampleLoadResponse(status: String): LaundryLoadResponseDto {
    return LaundryLoadResponseDto(
        id = "load-$status",
        washerId = "washer-1",
        clothingType = "MIXED",
        washingProgram = "NORMAL",
        status = status,
        locationId = "home",
        createdAt = "2026-07-05T14:00:00Z",
        startedAt = null,
        completedAt = if (status == "COMPLETED") "2026-07-05T18:00:00Z" else null,
        prediction = null,
    )
}
