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
import com.tesis_pro.tenderapp.domain.model.SpinSpeedRpm
import com.tesis_pro.tenderapp.domain.model.WasherType
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteWasherDataSourceTest {
    @Test
    fun listWashers_mapsBackendDtosToDomainModels() = runBlocking {
        val api = FakeWasherBackendApi(
            washers = listOf(sampleWasherResponse(id = "washer-1", isPrimary = true)),
        )
        val dataSource = RemoteWasherDataSource(api)

        val result = dataSource.listWashers("token-1")

        assertTrue(result is RemoteDataResult.Success)
        val washer = (result as RemoteDataResult.Success).data.single()
        assertEquals("Bearer token-1", api.lastAuthorization)
        assertEquals("washer-1", washer.id)
        assertEquals("Main washer", washer.name)
        assertEquals(WasherType.FRONT_LOAD, washer.type)
        assertEquals(7.0, washer.capacityKg ?: 0.0, 0.01)
        assertEquals(SpinSpeedRpm.RPM_1200, washer.defaultSpinRpm)
        assertEquals(true, washer.isPrimary)
    }

    @Test
    fun listWashers_mapsLegacyOrLocalizedWasherTypeAliases() = runBlocking {
        val api = FakeWasherBackendApi(
            washers = listOf(
                sampleWasherResponse(id = "washer-front", type = "front load"),
                sampleWasherResponse(id = "washer-spanish", type = "Carga frontal"),
                sampleWasherResponse(id = "washer-combo", type = "Lavarropas secarropas"),
            ),
        )
        val dataSource = RemoteWasherDataSource(api)

        val result = dataSource.listWashers("token-1")

        assertTrue(result is RemoteDataResult.Success)
        val washers = (result as RemoteDataResult.Success).data
        assertEquals(WasherType.FRONT_LOAD, washers[0].type)
        assertEquals(WasherType.FRONT_LOAD, washers[1].type)
        assertEquals(WasherType.WASHER_DRYER, washers[2].type)
    }

    @Test
    fun createWasher_sendsBackendRequestAndMapsResponse() = runBlocking {
        val api = FakeWasherBackendApi(createdWasher = sampleWasherResponse(id = "washer-created"))
        val dataSource = RemoteWasherDataSource(api)

        val result = dataSource.createWasher(
            accessToken = "token-1",
            request = CreateRemoteWasher(
                name = "Balcony washer",
                type = WasherType.TOP_LOAD,
                capacityKg = 5.0,
                energyLabel = "A",
                waterUsageLiters = 38.0,
                defaultSpinRpm = SpinSpeedRpm.RPM_1000,
                isPrimary = false,
            ),
        )

        assertTrue(result is RemoteDataResult.Success)
        assertEquals("Bearer token-1", api.lastAuthorization)
        assertEquals(
            SaveWasherRequestDto(
                name = "Balcony washer",
                type = "TOP_LOAD",
                capacityKg = 5.0,
                energyLabel = "A",
                waterUsageLiters = 38.0,
                defaultSpinRpm = 1000,
                isPrimary = false,
            ),
            api.lastCreateRequest,
        )
    }

    @Test
    fun updateWasher_sendsBackendPutRequestAndMapsResponse() = runBlocking {
        val api = FakeWasherBackendApi(updatedWasher = sampleWasherResponse(id = "washer-1"))
        val dataSource = RemoteWasherDataSource(api)

        val result = dataSource.updateWasher(
            accessToken = "token-1",
            washerId = "washer-1",
            request = CreateRemoteWasher(
                name = "Updated washer",
                type = WasherType.WASHER_DRYER,
                capacityKg = 8.0,
                energyLabel = "A++",
                waterUsageLiters = 41.0,
                defaultSpinRpm = SpinSpeedRpm.RPM_1400,
                isPrimary = true,
            ),
        )

        assertTrue(result is RemoteDataResult.Success)
        assertEquals("Bearer token-1", api.lastAuthorization)
        assertEquals("washer-1", api.lastUpdatedWasherId)
        assertEquals(
            SaveWasherRequestDto(
                name = "Updated washer",
                type = "WASHER_DRYER",
                capacityKg = 8.0,
                energyLabel = "A++",
                waterUsageLiters = 41.0,
                defaultSpinRpm = 1400,
                isPrimary = true,
            ),
            api.lastUpdateRequest,
        )
    }

    @Test
    fun listWashers_mapsNetworkErrors() = runBlocking {
        val dataSource = RemoteWasherDataSource(
            FakeWasherBackendApi(listError = IOException("offline")),
        )

        val result = dataSource.listWashers("token-1")

        assertEquals(RemoteDataErrorType.NETWORK, (result as RemoteDataResult.Error).type)
    }

    @Test
    fun retireWasher_sendsBackendDeleteRequest() = runBlocking {
        val api = FakeWasherBackendApi()
        val dataSource = RemoteWasherDataSource(api)

        val result = dataSource.retireWasher("token-1", "washer-1")

        assertTrue(result is RemoteDataResult.Success)
        assertEquals("Bearer token-1", api.lastAuthorization)
        assertEquals("washer-1", api.lastRetiredWasherId)
    }

    @Test
    fun listWashers_rejectsBlankTokenBeforeCallingBackend() = runBlocking {
        val api = FakeWasherBackendApi()
        val dataSource = RemoteWasherDataSource(api)

        val result = dataSource.listWashers(" ")

        assertEquals(RemoteDataErrorType.AUTHENTICATION, (result as RemoteDataResult.Error).type)
        assertEquals(null, api.lastAuthorization)
    }
}

private class FakeWasherBackendApi(
    private val washers: List<WasherResponseDto> = emptyList(),
    private val createdWasher: WasherResponseDto = sampleWasherResponse(id = "washer-created"),
    private val updatedWasher: WasherResponseDto = sampleWasherResponse(id = "washer-updated"),
    private val listError: Exception? = null,
) : TenderBackendApi {
    var lastAuthorization: String? = null
    var lastCreateRequest: SaveWasherRequestDto? = null
    var lastUpdateRequest: SaveWasherRequestDto? = null
    var lastUpdatedWasherId: String? = null
    var lastRetiredWasherId: String? = null

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

    override suspend fun listWashers(authorization: String): List<WasherResponseDto> {
        listError?.let { throw it }
        lastAuthorization = authorization
        return washers
    }

    override suspend fun createWasher(
        authorization: String,
        request: SaveWasherRequestDto,
    ): WasherResponseDto {
        lastAuthorization = authorization
        lastCreateRequest = request
        return createdWasher
    }

    override suspend fun updateWasher(
        authorization: String,
        washerId: String,
        request: SaveWasherRequestDto,
    ): WasherResponseDto {
        lastAuthorization = authorization
        lastUpdatedWasherId = washerId
        lastUpdateRequest = request
        return updatedWasher
    }

    override suspend fun retireWasher(
        authorization: String,
        washerId: String,
    ) {
        lastAuthorization = authorization
        lastRetiredWasherId = washerId
    }

    override suspend fun currentWeather(
        authorization: String,
        locationId: String,
    ): WeatherSnapshotResponseDto {
        error("Not used by washer data source tests")
    }

    override suspend fun listLaundryLoads(authorization: String): List<LaundryLoadResponseDto> {
        error("Not used by washer data source tests")
    }

    override suspend fun createLaundryLoad(
        authorization: String,
        request: CreateLaundryLoadRequestDto,
    ): LaundryLoadResponseDto {
        error("Not used by washer data source tests")
    }

    override suspend fun updateLaundryLoadStatus(
        authorization: String,
        loadId: String,
        request: UpdateLaundryLoadStatusRequestDto,
    ): LaundryLoadResponseDto {
        error("Not used by washer data source tests")
    }

    override suspend fun saveUserLocation(
        authorization: String,
        request: SaveUserLocationRequestDto,
    ): UserLocationResponseDto {
        error("Not used by washer data source tests")
    }
}

private fun sampleWasherResponse(
    id: String,
    type: String = "FRONT_LOAD",
    isPrimary: Boolean = false,
): WasherResponseDto {
    return WasherResponseDto(
        id = id,
        name = "Main washer",
        type = type,
        capacityKg = 7.0,
        energyLabel = "A",
        waterUsageLiters = 45.0,
        defaultSpinRpm = 1200,
        isPrimary = isPrimary,
    )
}
