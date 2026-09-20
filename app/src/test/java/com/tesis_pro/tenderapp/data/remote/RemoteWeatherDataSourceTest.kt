package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.CreateLaundryLoadRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.HealthResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.LaundryLoadResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.SaveWasherRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.SaveUserLocationRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.SupabaseHealthResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.UpdateLaundryLoadStatusRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.UserLocationResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.WeatherLocationDto
import com.tesis_pro.tenderapp.data.remote.dto.WeatherSnapshotResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.WasherResponseDto
import com.tesis_pro.tenderapp.domain.model.WeatherCondition
import com.tesis_pro.tenderapp.domain.model.WeatherDataSource
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteWeatherDataSourceTest {
    @Test
    fun currentWeather_mapsBackendDtoToDomainModel() = runBlocking {
        val api = FakeWeatherBackendApi()
        val dataSource = RemoteWeatherDataSource(api)

        val result = dataSource.currentWeather(
            accessToken = "token-1",
            locationId = "home",
        )

        assertTrue(result is RemoteDataResult.Success)
        val weather = (result as RemoteDataResult.Success).data
        assertEquals("Bearer token-1", api.lastAuthorization)
        assertEquals("home", api.lastLocationId)
        assertEquals(WeatherCondition.CLEAR, weather.condition)
        assertEquals(WeatherDataSource.OPEN_METEO, weather.source)
        assertEquals(false, weather.isStale)
        assertEquals(24.0, weather.temperatureCelsius, 0.01)
        assertEquals(1_783_260_000_000L, weather.capturedAtEpochMillis)
    }

    @Test
    fun currentWeather_mapsNetworkErrors() = runBlocking {
        val dataSource = RemoteWeatherDataSource(
            FakeWeatherBackendApi(weatherError = IOException("offline")),
        )

        val result = dataSource.currentWeather("token-1", "home")

        assertEquals(RemoteDataErrorType.NETWORK, (result as RemoteDataResult.Error).type)
    }

    @Test
    fun currentWeather_preservesBackendStaleFlag() = runBlocking {
        val dataSource = RemoteWeatherDataSource(
            FakeWeatherBackendApi(source = "MOCK", isStale = true),
        )

        val result = dataSource.currentWeather("token-1", "home")

        assertTrue(result is RemoteDataResult.Success)
        val weather = (result as RemoteDataResult.Success).data
        assertEquals(WeatherDataSource.MOCK, weather.source)
        assertEquals(true, weather.isStale)
    }

    @Test
    fun currentWeather_mapsMetNoSource() = runBlocking {
        val dataSource = RemoteWeatherDataSource(
            FakeWeatherBackendApi(source = "MET_NO"),
        )

        val result = dataSource.currentWeather("token-1", "home")

        assertTrue(result is RemoteDataResult.Success)
        val weather = (result as RemoteDataResult.Success).data
        assertEquals(WeatherDataSource.MET_NO, weather.source)
    }

    @Test
    fun currentWeather_rejectsBlankTokenBeforeCallingBackend() = runBlocking {
        val api = FakeWeatherBackendApi()
        val dataSource = RemoteWeatherDataSource(api)

        val result = dataSource.currentWeather(" ", "home")

        assertEquals(RemoteDataErrorType.AUTHENTICATION, (result as RemoteDataResult.Error).type)
        assertEquals(null, api.lastAuthorization)
    }
}

private class FakeWeatherBackendApi(
    private val weatherError: Exception? = null,
    private val source: String = "OPEN_METEO",
    private val isStale: Boolean = false,
) : TenderBackendApi {
    var lastAuthorization: String? = null
    var lastLocationId: String? = null

    override suspend fun health(): HealthResponseDto {
        return HealthResponseDto(status = "ok", version = "test")
    }

    override suspend fun supabaseHealth(): SupabaseHealthResponseDto {
        return SupabaseHealthResponseDto(
            status = "ok",
            projectHost = "example.supabase.co",
            checkedAt = "2026-07-05T14:00:00Z",
            message = null,
        )
    }

    override suspend fun currentWeather(
        authorization: String,
        locationId: String,
    ): WeatherSnapshotResponseDto {
        weatherError?.let { throw it }
        lastAuthorization = authorization
        lastLocationId = locationId
        return WeatherSnapshotResponseDto(
            location = WeatherLocationDto(
                id = "home",
                label = "Home patio",
                latitude = -34.6037,
                longitude = -58.3816,
            ),
            source = source,
            capturedAt = "2026-07-05T14:00:00Z",
            forecastFor = "2026-07-05T14:00:00Z",
            condition = "CLEAR",
            temperatureCelsius = 24.0,
            humidityPercent = 48,
            windSpeedKph = 18.0,
            rainProbabilityPercent = 8,
            cloudCoverPercent = 20,
            isStale = isStale,
        )
    }

    override suspend fun listLaundryLoads(authorization: String): List<LaundryLoadResponseDto> {
        error("Not used by weather data source tests")
    }

    override suspend fun createLaundryLoad(
        authorization: String,
        request: CreateLaundryLoadRequestDto,
    ): LaundryLoadResponseDto {
        error("Not used by weather data source tests")
    }

    override suspend fun updateLaundryLoadStatus(
        authorization: String,
        loadId: String,
        request: UpdateLaundryLoadStatusRequestDto,
    ): LaundryLoadResponseDto {
        error("Not used by weather data source tests")
    }

    override suspend fun listWashers(authorization: String): List<WasherResponseDto> {
        error("Not used by weather data source tests")
    }

    override suspend fun createWasher(
        authorization: String,
        request: SaveWasherRequestDto,
    ): WasherResponseDto {
        error("Not used by weather data source tests")
    }

    override suspend fun updateWasher(
        authorization: String,
        washerId: String,
        request: SaveWasherRequestDto,
    ): WasherResponseDto {
        error("Not used by weather data source tests")
    }

    override suspend fun retireWasher(
        authorization: String,
        washerId: String,
    ) {
        error("Not used by weather data source tests")
    }

    override suspend fun saveUserLocation(
        authorization: String,
        request: SaveUserLocationRequestDto,
    ): UserLocationResponseDto {
        error("Not used by weather data source tests")
    }
}
