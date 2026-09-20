package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.CreateLaundryLoadRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.HealthResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.LaundryLoadResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.SaveWasherRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.SaveUserLocationRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.SupabaseHealthResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.UpdateLaundryLoadStatusRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.UserLocationResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.WeatherSnapshotResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.WasherResponseDto
import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostConfidence
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostLevel
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.model.LoadSize
import com.tesis_pro.tenderapp.domain.model.SpinSpeedRpm
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import java.io.IOException
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class RemoteLaundryDataSourceTest {
    @Test
    fun listLaundryLoads_mapsBackendDtosToDomainModels() = runBlocking {
        val api = FakeTenderBackendApi(
            loads = listOf(
                LaundryLoadResponseDto(
                    id = "load-1",
                    washerId = "washer-1",
                    clothingType = "MIXED",
                    washingProgram = "NORMAL",
                    status = "PLANNED",
                    locationId = "home",
                    spinRpm = 1200,
                    loadSize = "MEDIUM",
                    estimatedWashingEnergyKwh = 0.65,
                    estimatedWashingWaterLiters = 44.0,
                    estimatedWashingCostLevel = "LOW",
                    estimatedWashingCostConfidence = "MEDIUM",
                    createdAt = "2026-07-05T14:00:00Z",
                    startedAt = null,
                    dryingStartedAt = "2026-07-05T15:30:00Z",
                    dryingEstimatedMinutesAtStart = 120,
                    dryingEstimatedPickupAt = "2026-07-05T17:30:00Z",
                    completedAt = null,
                    prediction = null,
                ),
            ),
        )
        val dataSource = RemoteLaundryDataSource(api)

        val result = dataSource.listLaundryLoads("token-1")

        assertTrue(result is RemoteDataResult.Success)
        val load = (result as RemoteDataResult.Success).data.single()
        assertEquals("Bearer token-1", api.lastAuthorization)
        assertEquals("load-1", load.id)
        assertEquals(ClothingType.MIXED, load.clothingType)
        assertEquals(WashingProgram.NORMAL, load.washingProgram)
        assertEquals(LaundryLoadStatus.PLANNED, load.status)
        assertEquals(SpinSpeedRpm.RPM_1200, load.spinRpm)
        assertEquals(LoadSize.MEDIUM, load.loadSize)
        assertEquals(EstimatedWashingCostLevel.LOW, load.estimatedWashingCost?.level)
        assertEquals(EstimatedWashingCostConfidence.MEDIUM, load.estimatedWashingCost?.confidence)
        assertEquals(1_783_260_000_000L, load.createdAtEpochMillis)
        assertEquals(1_783_265_400_000L, load.dryingStartedAtEpochMillis)
        assertEquals(120, load.dryingEstimatedMinutesAtStart)
        assertEquals(1_783_272_600_000L, load.dryingEstimatedPickupAtEpochMillis)
    }

    @Test
    fun createLaundryLoad_sendsBackendRequestAndMapsResponse() = runBlocking {
        val api = FakeTenderBackendApi(
            createdLoad = sampleLoadResponse(id = "load-created"),
        )
        val dataSource = RemoteLaundryDataSource(api)

        val result = dataSource.createLaundryLoad(
            accessToken = "token-1",
            request = CreateRemoteLaundryLoad(
                washerId = null,
                clothingType = ClothingType.DELICATES,
                washingProgram = WashingProgram.DELICATE,
                locationId = "balcony",
                dryingLocation = DryingLocation.BALCONY,
                spinRpm = SpinSpeedRpm.RPM_800,
                loadSize = LoadSize.SMALL,
            ),
        )

        assertTrue(result is RemoteDataResult.Success)
        assertEquals("Bearer token-1", api.lastAuthorization)
        assertEquals(
            CreateLaundryLoadRequestDto(
                washerId = null,
                clothingType = "DELICATES",
                washingProgram = "DELICATE",
                locationId = "balcony",
                dryingLocationId = "BALCONY",
                spinRpm = 800,
                loadSize = "SMALL",
            ),
            api.lastCreateRequest,
        )
    }

    @Test
    fun updateLaundryLoadStatus_sendsStatusPatch() = runBlocking {
        val api = FakeTenderBackendApi(
            updatedLoad = sampleLoadResponse(id = "load-2", status = "DRYING"),
        )
        val dataSource = RemoteLaundryDataSource(api)

        val result = dataSource.updateLaundryLoadStatus(
            accessToken = "token-1",
            loadId = "load-2",
            status = LaundryLoadStatus.DRYING,
        )

        assertTrue(result is RemoteDataResult.Success)
        assertEquals("load-2", api.lastUpdatedLoadId)
        assertEquals(UpdateLaundryLoadStatusRequestDto(status = "DRYING"), api.lastUpdateRequest)
    }

    @Test
    fun listLaundryLoads_mapsNetworkErrors() = runBlocking {
        val dataSource = RemoteLaundryDataSource(
            FakeTenderBackendApi(listError = IOException("offline")),
        )

        val result = dataSource.listLaundryLoads("token-1")

        assertEquals(
            RemoteDataResult.Error(
                type = RemoteDataErrorType.NETWORK,
                message = "Unable to reach TenderApp backend.",
            ),
            result,
        )
    }

    @Test
    fun listLaundryLoads_mapsUnauthorizedErrors() = runBlocking {
        val dataSource = RemoteLaundryDataSource(
            FakeTenderBackendApi(listError = httpException(401)),
        )

        val result = dataSource.listLaundryLoads("token-1")

        assertEquals(RemoteDataErrorType.AUTHENTICATION, (result as RemoteDataResult.Error).type)
    }

    @Test
    fun listLaundryLoads_rejectsBlankTokenBeforeCallingBackend() = runBlocking {
        val api = FakeTenderBackendApi()
        val dataSource = RemoteLaundryDataSource(api)

        val result = dataSource.listLaundryLoads(" ")

        assertEquals(RemoteDataErrorType.AUTHENTICATION, (result as RemoteDataResult.Error).type)
        assertEquals(null, api.lastAuthorization)
    }
}

private class FakeTenderBackendApi(
    private val loads: List<LaundryLoadResponseDto> = emptyList(),
    private val createdLoad: LaundryLoadResponseDto = sampleLoadResponse(id = "load-created"),
    private val updatedLoad: LaundryLoadResponseDto = sampleLoadResponse(id = "load-updated"),
    private val listError: Exception? = null,
) : TenderBackendApi {
    var lastAuthorization: String? = null
    var lastCreateRequest: CreateLaundryLoadRequestDto? = null
    var lastUpdatedLoadId: String? = null
    var lastUpdateRequest: UpdateLaundryLoadStatusRequestDto? = null

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

    override suspend fun listLaundryLoads(authorization: String): List<LaundryLoadResponseDto> {
        listError?.let { throw it }
        lastAuthorization = authorization
        return loads
    }

    override suspend fun createLaundryLoad(
        authorization: String,
        request: CreateLaundryLoadRequestDto,
    ): LaundryLoadResponseDto {
        lastAuthorization = authorization
        lastCreateRequest = request
        return createdLoad
    }

    override suspend fun updateLaundryLoadStatus(
        authorization: String,
        loadId: String,
        request: UpdateLaundryLoadStatusRequestDto,
    ): LaundryLoadResponseDto {
        lastAuthorization = authorization
        lastUpdatedLoadId = loadId
        lastUpdateRequest = request
        return updatedLoad
    }

    override suspend fun currentWeather(
        authorization: String,
        locationId: String,
    ): WeatherSnapshotResponseDto {
        error("Not used by laundry data source tests")
    }

    override suspend fun listWashers(authorization: String): List<WasherResponseDto> {
        error("Not used by laundry data source tests")
    }

    override suspend fun createWasher(
        authorization: String,
        request: SaveWasherRequestDto,
    ): WasherResponseDto {
        error("Not used by laundry data source tests")
    }

    override suspend fun updateWasher(
        authorization: String,
        washerId: String,
        request: SaveWasherRequestDto,
    ): WasherResponseDto {
        error("Not used by laundry data source tests")
    }

    override suspend fun retireWasher(
        authorization: String,
        washerId: String,
    ) {
        error("Not used by laundry data source tests")
    }

    override suspend fun saveUserLocation(
        authorization: String,
        request: SaveUserLocationRequestDto,
    ): UserLocationResponseDto {
        error("Not used by laundry data source tests")
    }
}

private fun sampleLoadResponse(
    id: String,
    status: String = "PLANNED",
): LaundryLoadResponseDto {
    return LaundryLoadResponseDto(
        id = id,
        washerId = "washer-1",
        clothingType = "MIXED",
        washingProgram = "NORMAL",
        status = status,
        locationId = "home",
        spinRpm = 1200,
        loadSize = "MEDIUM",
        estimatedWashingEnergyKwh = 0.65,
        estimatedWashingWaterLiters = 44.0,
        estimatedWashingCostLevel = "LOW",
        estimatedWashingCostConfidence = "MEDIUM",
        createdAt = "2026-07-05T14:00:00Z",
        startedAt = null,
        dryingStartedAt = null,
        dryingEstimatedMinutesAtStart = null,
        dryingEstimatedPickupAt = null,
        completedAt = null,
        prediction = null,
    )
}

private fun httpException(code: Int): HttpException {
    return HttpException(Response.error<Unit>(code, "".toResponseBody(null)))
}
