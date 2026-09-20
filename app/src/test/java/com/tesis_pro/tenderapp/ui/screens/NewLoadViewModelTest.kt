package com.tesis_pro.tenderapp.ui.screens

import com.tesis_pro.tenderapp.data.remote.RemoteLaundryDataSource
import com.tesis_pro.tenderapp.data.remote.RemoteWasherDataSource
import com.tesis_pro.tenderapp.data.remote.CreateRemoteCompletionPlan
import com.tesis_pro.tenderapp.data.remote.RemoteDataResult
import com.tesis_pro.tenderapp.data.remote.TenderBackendApi
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
import com.tesis_pro.tenderapp.data.repository.BackendLaundryRepository
import com.tesis_pro.tenderapp.data.repository.BackendWasherRepository
import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.DryingMethod
import com.tesis_pro.tenderapp.domain.model.DryingPrediction
import com.tesis_pro.tenderapp.domain.model.DryingVerdict
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCost
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostConfidence
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostLevel
import com.tesis_pro.tenderapp.domain.model.LoadSize
import com.tesis_pro.tenderapp.domain.model.LaundryCompletionPlan
import com.tesis_pro.tenderapp.domain.model.ProgramCompletionOption
import com.tesis_pro.tenderapp.domain.model.SpinSpeedRpm
import com.tesis_pro.tenderapp.domain.model.WeatherCondition
import com.tesis_pro.tenderapp.domain.model.WeatherDataSource
import com.tesis_pro.tenderapp.domain.model.WeatherSnapshot
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.repository.HouseholdSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NewLoadViewModelTest {
    @Test
    fun decisionPreview_updatesWhenSelectionChanges() {
        val api = FakeNewLoadBackendApi()
        val viewModel = newLoadViewModel(api = api)

        val initialPreview = viewModel.uiState.value.decisionPreview

        viewModel.selectClothingType(ClothingType.BEDDING)
        viewModel.selectWashingProgram(WashingProgram.DELICATE)
        viewModel.selectDryingLocation(DryingLocation.INDOOR)

        val updatedPreview = viewModel.uiState.value.decisionPreview
        assertEquals(NewLoadDecisionProfile.INDOOR_STABLE, updatedPreview.profile)
        assertTrue(updatedPreview.estimatedDryingMinutes > initialPreview.estimatedDryingMinutes)
    }

    @Test
    fun decisionPreview_recommendsLightOutdoorEcoSelection() {
        val api = FakeNewLoadBackendApi()
        val viewModel = newLoadViewModel(api = api)

        viewModel.selectClothingType(ClothingType.LIGHT_CLOTHES)
        viewModel.selectWashingProgram(WashingProgram.ECO)
        viewModel.selectDryingLocation(DryingLocation.PATIO)

        val preview = viewModel.uiState.value.decisionPreview
        assertEquals(NewLoadDecisionProfile.RECOMMENDED, preview.profile)
        assertEquals(126, preview.estimatedDryingMinutes)
    }

    @Test
    fun decisionPreview_usesCurrentWeatherWhenAvailable() {
        val state = NewLoadScreenState(
            selectedClothingType = ClothingType.MIXED,
            selectedWashingProgram = WashingProgram.ECO,
            selectedDryingLocation = DryingLocation.PATIO,
            weatherSnapshot = WeatherSnapshot(
                capturedAtEpochMillis = 1_783_700_000_000,
                forecastForEpochMillis = 1_783_700_000_000,
                location = WeatherLocation(
                    id = "home",
                    label = "Home",
                    latitude = -34.6037,
                    longitude = -58.3816,
                ),
                condition = WeatherCondition.CLOUDY,
                temperatureCelsius = 14.0,
                humidityPercent = 84,
                windSpeedKph = 4.0,
                rainProbabilityPercent = 0,
                cloudCoverPercent = 80,
                source = WeatherDataSource.OPEN_METEO,
            ),
        )

        val preview = state.decisionPreview

        assertEquals(NewLoadDecisionSource.OPEN_METEO, preview.source)
        assertEquals(NewLoadDecisionProfile.MONITOR, preview.profile)
        assertTrue(preview.selectionScore < 90)
    }

    @Test
    fun decisionPreview_marksMetNoWeatherSourceWhenAvailable() {
        val state = NewLoadScreenState(
            selectedClothingType = ClothingType.MIXED,
            selectedWashingProgram = WashingProgram.ECO,
            selectedDryingLocation = DryingLocation.PATIO,
            weatherSnapshot = WeatherSnapshot(
                capturedAtEpochMillis = 1_783_700_000_000,
                forecastForEpochMillis = 1_783_700_000_000,
                location = WeatherLocation(
                    id = "home",
                    label = "Home",
                    latitude = -34.6037,
                    longitude = -58.3816,
                ),
                condition = WeatherCondition.CLOUDY,
                temperatureCelsius = 12.0,
                humidityPercent = 92,
                windSpeedKph = 12.0,
                rainProbabilityPercent = 0,
                cloudCoverPercent = 80,
                source = WeatherDataSource.MET_NO,
            ),
        )

        val preview = state.decisionPreview

        assertEquals(NewLoadDecisionSource.MET_NO, preview.source)
    }

    @Test
    fun decisionPreview_prefersBackendPredictionOverLocalWeather() {
        val weather = WeatherSnapshot(
            capturedAtEpochMillis = 1_783_700_000_000,
            forecastForEpochMillis = 1_783_700_000_000,
            location = WeatherLocation(
                id = "home",
                label = "Home",
                latitude = -34.6037,
                longitude = -58.3816,
            ),
            condition = WeatherCondition.CLOUDY,
            temperatureCelsius = 14.0,
            humidityPercent = 84,
            windSpeedKph = 4.0,
            rainProbabilityPercent = 0,
            cloudCoverPercent = 80,
            source = WeatherDataSource.OPEN_METEO,
        )
        val state = NewLoadScreenState(
            selectedClothingType = ClothingType.MIXED,
            selectedWashingProgram = WashingProgram.ECO,
            selectedDryingLocation = DryingLocation.PATIO,
            weatherSnapshot = weather,
            backendPredictionPreview = DryingPrediction(
                verdict = DryingVerdict.BAD,
                method = DryingMethod.OUTDOOR,
                estimatedDryingMinutes = 610,
                recommendedHangAtEpochMillis = weather.forecastForEpochMillis,
                estimatedPickupAtEpochMillis = weather.forecastForEpochMillis + 610L * 60_000L,
                suitabilityScore = 59,
                reason = "Backend prediction test reason.",
                weatherSnapshot = weather,
                estimatedCost = EstimatedWashingCost(
                    amount = null,
                    currency = null,
                    level = EstimatedWashingCostLevel.LOW,
                    confidence = EstimatedWashingCostConfidence.MEDIUM,
                    estimatedEnergyKwh = 0.72,
                    estimatedWaterLiters = 48.0,
                ),
            ),
        )

        val preview = state.decisionPreview

        assertEquals(NewLoadDecisionSource.BACKEND_PREDICTION, preview.source)
        assertEquals(NewLoadDecisionProfile.WAIT_OR_INDOOR, preview.profile)
        assertEquals(59, preview.selectionScore)
        assertEquals(610, preview.estimatedDryingMinutes)
        assertEquals(EstimatedWashingCostLevel.LOW, preview.estimatedCost?.level)
        assertEquals(0.72, preview.estimatedCost?.estimatedEnergyKwh ?: 0.0, 0.001)
    }

    @Test
    fun createDefaultLoad_sendsSelectedDryingLocationToBackend() {
        val api = FakeNewLoadBackendApi()
        val viewModel = newLoadViewModel(api = api)

        viewModel.selectClothingType(ClothingType.HEAVY_CLOTHES)
        viewModel.selectWashingProgram(WashingProgram.QUICK)
        viewModel.selectDryingLocation(DryingLocation.BALCONY)
        viewModel.selectSpinRpm(SpinSpeedRpm.RPM_1400)
        viewModel.selectLoadSize(LoadSize.LARGE)
        viewModel.createDefaultLoad()

        assertEquals(
            CreateLaundryLoadRequestDto(
                washerId = "washer-primary",
                clothingType = "HEAVY_CLOTHES",
                washingProgram = "QUICK",
                locationId = "home",
                dryingLocationId = "BALCONY",
                spinRpm = 1400,
                loadSize = "LARGE",
            ),
            api.lastCreateRequest,
        )
    }

    @Test
    fun refreshWashers_usesWasherDefaultSpinAsSmartDefault() {
        val api = FakeNewLoadBackendApi()
        val viewModel = newLoadViewModel(api = api)

        assertEquals(SpinSpeedRpm.RPM_1000, viewModel.uiState.value.resolvedSpinRpm)

        viewModel.selectSpinRpm(SpinSpeedRpm.RPM_1600)

        assertEquals(SpinSpeedRpm.RPM_1600, viewModel.uiState.value.resolvedSpinRpm)
    }

    @Test
    fun selectTargetReadyTime_usesNextLocalOccurrenceAndCrossesDayWhenNeeded() {
        val zone = ZoneId.systemDefault()
        val now = LocalDate.of(2026, 7, 25)
            .atTime(20, 0)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
        val viewModel = newLoadViewModel(
            api = FakeNewLoadBackendApi(),
            nowProvider = { now },
        )

        viewModel.selectTargetReadyTime(hour = 21, minute = 15)

        assertEquals(
            LocalDate.of(2026, 7, 25)
                .atTime(21, 15)
                .atZone(zone)
                .toInstant()
                .toEpochMilli(),
            viewModel.uiState.value.targetReadyAtEpochMillis,
        )

        viewModel.selectTargetReadyTime(hour = 19, minute = 30)

        assertEquals(
            LocalDate.of(2026, 7, 26)
                .atTime(19, 30)
                .atZone(zone)
                .toInstant()
                .toEpochMilli(),
            viewModel.uiState.value.targetReadyAtEpochMillis,
        )
    }

    @Test
    fun completionPlan_refreshesForRelevantInputsAndNotForProgramOnly() {
        val requests = mutableListOf<CreateRemoteCompletionPlan>()
        val now = LocalDate.of(2026, 7, 25)
            .atTime(LocalTime.of(20, 0))
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val viewModel = newLoadViewModel(
            api = FakeNewLoadBackendApi(),
            nowProvider = { now },
            completionPlanLoader = { _, request ->
                requests += request
                RemoteDataResult.Success(sampleCompletionPlan(request))
            },
        )
        requests.clear()

        viewModel.selectWasher("washer-primary")
        viewModel.selectClothingType(ClothingType.LIGHT_CLOTHES)
        viewModel.selectDryingLocation(DryingLocation.BALCONY)
        viewModel.selectSpinRpm(SpinSpeedRpm.RPM_1400)
        viewModel.selectLoadSize(LoadSize.LARGE)
        viewModel.selectTargetReadyTime(hour = 23, minute = 0)

        assertEquals(6, requests.size)
        assertEquals(ClothingType.LIGHT_CLOTHES, requests.last().clothingType)
        assertEquals(DryingLocation.BALCONY, requests.last().dryingLocation)
        assertEquals(SpinSpeedRpm.RPM_1400, requests.last().spinRpm)
        assertEquals(LoadSize.LARGE, requests.last().loadSize)

        viewModel.selectWashingProgram(WashingProgram.QUICK)

        assertEquals(6, requests.size)
        assertEquals(WashingProgram.QUICK, viewModel.uiState.value.selectedWashingProgram)
    }

    @Test
    fun completionPlan_selectionKeepsAllOptionsAndSelectsTappedProgram() {
        val now = LocalDate.of(2026, 7, 25)
            .atTime(20, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val viewModel = newLoadViewModel(
            api = FakeNewLoadBackendApi(),
            nowProvider = { now },
            completionPlanLoader = { _, request ->
                RemoteDataResult.Success(sampleCompletionPlan(request))
            },
        )

        viewModel.selectWashingProgram(WashingProgram.QUICK)

        assertEquals(WashingProgram.QUICK, viewModel.uiState.value.selectedWashingProgram)
        assertEquals(2, viewModel.uiState.value.backendCompletionPlan?.options?.size)
        assertTrue(viewModel.uiState.value.backendCompletionPlan?.options?.any { !it.feasible } == true)
    }

    @Test
    fun completionPlan_manualRetryRecoversAfterRemoteError() {
        var requestCount = 0
        var failNextRequest = false
        val viewModel = newLoadViewModel(
            api = FakeNewLoadBackendApi(),
            completionPlanLoader = { _, request ->
                requestCount += 1
                if (failNextRequest) {
                    failNextRequest = false
                    RemoteDataResult.Error(
                        type = com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType.SERVER,
                        message = "Temporary backend failure",
                    )
                } else {
                    RemoteDataResult.Success(sampleCompletionPlan(request))
                }
            },
        )

        val initialRequestCount = requestCount
        failNextRequest = true
        viewModel.refreshCompletionPlan()

        assertEquals("Temporary backend failure", viewModel.uiState.value.completionPlanError)

        viewModel.refreshCompletionPlan()

        assertEquals(initialRequestCount + 2, requestCount)
        assertEquals(null, viewModel.uiState.value.completionPlanError)
        assertEquals(
            listOf(WashingProgram.QUICK),
            viewModel.uiState.value.backendCompletionPlan?.recommendedPrograms,
        )
    }

    @Test
    fun completionPlan_ignoresAnOlderResponseAfterSelectionChanges() = runBlocking {
        val firstPendingResult = CompletableDeferred<RemoteDataResult<LaundryCompletionPlan>>()
        var deferNextRequest = false
        var deferredRequestSeen = false
        val viewModel = newLoadViewModel(
            api = FakeNewLoadBackendApi(),
            completionPlanLoader = { _, request ->
                if (deferNextRequest && !deferredRequestSeen) {
                    deferredRequestSeen = true
                    firstPendingResult.await()
                } else {
                    RemoteDataResult.Success(
                        sampleCompletionPlan(request).copy(
                            recommendedPrograms = listOf(WashingProgram.QUICK),
                        ),
                    )
                }
            },
        )

        deferNextRequest = true
        viewModel.selectClothingType(ClothingType.HEAVY_CLOTHES)
        viewModel.selectLoadSize(LoadSize.LARGE)

        assertEquals(
            listOf(WashingProgram.QUICK),
            viewModel.uiState.value.backendCompletionPlan?.recommendedPrograms,
        )

        val obsoleteRequest = CreateRemoteCompletionPlan(
            laundryLoadId = "new-load-preview",
            clothingType = ClothingType.HEAVY_CLOTHES,
            dryingMethod = DryingMethod.OUTDOOR,
            locationId = "home",
            dryingLocation = DryingLocation.PATIO,
            plannedStartAtEpochMillis = System.currentTimeMillis(),
            targetReadyAtEpochMillis = System.currentTimeMillis() + 6L * 60L * 60_000L,
        )
        firstPendingResult.complete(
            RemoteDataResult.Success(
                sampleCompletionPlan(obsoleteRequest).copy(
                    recommendedPrograms = listOf(WashingProgram.ECO),
                ),
            ),
        )

        assertEquals(
            listOf(WashingProgram.QUICK),
            viewModel.uiState.value.backendCompletionPlan?.recommendedPrograms,
        )
    }
}

private fun newLoadViewModel(
    api: FakeNewLoadBackendApi,
    nowProvider: () -> Long = System::currentTimeMillis,
    completionPlanLoader: (suspend (String, CreateRemoteCompletionPlan) -> RemoteDataResult<LaundryCompletionPlan>)? = null,
): NewLoadViewModel {
    return NewLoadViewModel(
        laundryRepository = BackendLaundryRepository(
            laundryDataSource = RemoteLaundryDataSource(api),
            accessTokenProvider = { "token-1" },
        ),
        washerRepository = BackendWasherRepository(
            washerDataSource = RemoteWasherDataSource(api),
            accessTokenProvider = { "token-1" },
        ),
        accessTokenProvider = { "token-1" },
        householdSettingsRepository = FakeHouseholdSettingsRepository(),
        completionPlanLoader = completionPlanLoader,
        nowProvider = nowProvider,
        coroutineScope = CoroutineScope(Dispatchers.Unconfined),
    )
}

private fun sampleCompletionPlan(request: CreateRemoteCompletionPlan): LaundryCompletionPlan {
    return LaundryCompletionPlan(
        generatedAtEpochMillis = request.plannedStartAtEpochMillis,
        plannedStartAtEpochMillis = request.plannedStartAtEpochMillis,
        targetReadyAtEpochMillis = request.targetReadyAtEpochMillis,
        weatherSource = WeatherDataSource.OPEN_METEO,
        isStale = false,
        forecastCoverageEndsAtEpochMillis = request.targetReadyAtEpochMillis + 60L * 60_000L,
        recommendedPrograms = listOf(WashingProgram.QUICK),
        options = listOf(
            ProgramCompletionOption(
                program = WashingProgram.QUICK,
                washingMinutes = 30,
                washingEndsAtEpochMillis = request.plannedStartAtEpochMillis + 30L * 60_000L,
                dryingStartsAtEpochMillis = request.plannedStartAtEpochMillis + 30L * 60_000L,
                estimatedDryingMinutes = 90,
                estimatedReadyAtEpochMillis = request.plannedStartAtEpochMillis + 120L * 60_000L,
                totalElapsedMinutes = 120,
                marginMinutes = 60,
                feasible = true,
                usesForecastExtrapolation = false,
                verdict = DryingVerdict.GOOD,
                suitabilityScore = 88,
            ),
            ProgramCompletionOption(
                program = WashingProgram.ECO,
                washingMinutes = 90,
                washingEndsAtEpochMillis = request.plannedStartAtEpochMillis + 90L * 60_000L,
                dryingStartsAtEpochMillis = request.plannedStartAtEpochMillis + 90L * 60_000L,
                estimatedDryingMinutes = 150,
                estimatedReadyAtEpochMillis = request.plannedStartAtEpochMillis + 240L * 60_000L,
                totalElapsedMinutes = 240,
                marginMinutes = -60,
                feasible = false,
                usesForecastExtrapolation = true,
                verdict = DryingVerdict.CAUTION,
                suitabilityScore = 61,
            ),
        ),
    )
}

private class FakeHouseholdSettingsRepository : HouseholdSettingsRepository {
    override fun getWeatherLocation(): WeatherLocation {
        return WeatherLocation(
            id = "home",
            label = "Home",
            latitude = -34.6037,
            longitude = -58.3816,
        )
    }
}

private class FakeNewLoadBackendApi : TenderBackendApi {
    var lastCreateRequest: CreateLaundryLoadRequestDto? = null

    override suspend fun health(): HealthResponseDto {
        error("Not used by new load view model tests")
    }

    override suspend fun supabaseHealth(): SupabaseHealthResponseDto {
        error("Not used by new load view model tests")
    }

    override suspend fun currentWeather(
        authorization: String,
        locationId: String,
    ): WeatherSnapshotResponseDto {
        error("Not used by new load view model tests")
    }

    override suspend fun listLaundryLoads(authorization: String): List<LaundryLoadResponseDto> {
        error("Not used by new load view model tests")
    }

    override suspend fun createLaundryLoad(
        authorization: String,
        request: CreateLaundryLoadRequestDto,
    ): LaundryLoadResponseDto {
        lastCreateRequest = request
        return sampleLoadResponse()
    }

    override suspend fun updateLaundryLoadStatus(
        authorization: String,
        loadId: String,
        request: UpdateLaundryLoadStatusRequestDto,
    ): LaundryLoadResponseDto {
        error("Not used by new load view model tests")
    }

    override suspend fun listWashers(authorization: String): List<WasherResponseDto> {
        return listOf(
            WasherResponseDto(
                id = "washer-primary",
                name = "Primary washer",
                type = "FRONT_LOAD",
                capacityKg = 8.0,
                energyLabel = "A",
                waterUsageLiters = 42.0,
                defaultSpinRpm = 1000,
                isPrimary = true,
            ),
        )
    }

    override suspend fun createWasher(
        authorization: String,
        request: SaveWasherRequestDto,
    ): WasherResponseDto {
        error("Not used by new load view model tests")
    }

    override suspend fun updateWasher(
        authorization: String,
        washerId: String,
        request: SaveWasherRequestDto,
    ): WasherResponseDto {
        error("Not used by new load view model tests")
    }

    override suspend fun retireWasher(
        authorization: String,
        washerId: String,
    ) {
        error("Not used by new load view model tests")
    }

    override suspend fun saveUserLocation(
        authorization: String,
        request: SaveUserLocationRequestDto,
    ): UserLocationResponseDto {
        error("Not used by new load view model tests")
    }
}

private fun sampleLoadResponse(): LaundryLoadResponseDto {
    return LaundryLoadResponseDto(
        id = "load-1",
        washerId = "washer-primary",
        clothingType = "HEAVY_CLOTHES",
        washingProgram = "QUICK",
        status = "PLANNED",
        locationId = "home",
        dryingLocationId = "BALCONY",
        spinRpm = 1400,
        loadSize = "LARGE",
        createdAt = "2026-07-05T14:00:00Z",
        startedAt = null,
        completedAt = null,
        prediction = null,
    )
}
