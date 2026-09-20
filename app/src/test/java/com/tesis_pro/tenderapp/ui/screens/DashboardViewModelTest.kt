package com.tesis_pro.tenderapp.ui.screens

import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.DryingMethod
import com.tesis_pro.tenderapp.domain.model.DryingPrediction
import com.tesis_pro.tenderapp.domain.model.DryingVerdict
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCost
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostConfidence
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostLevel
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.domain.model.WeatherDataSource
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.model.WeatherSnapshot
import com.tesis_pro.tenderapp.data.repository.LaundryMutationResult
import com.tesis_pro.tenderapp.data.repository.LaundryStatusUpdater
import com.tesis_pro.tenderapp.domain.repository.DashboardLoadResult
import com.tesis_pro.tenderapp.domain.repository.DashboardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardViewModelTest {
    @Test
    fun uiState_exposesBackendDrivenDashboardRecommendation() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(),
                    weatherSnapshot = sampleWeather(),
                    sourceLabel = "Backend data",
                )
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )
        val state = viewModel.uiState.value

        assertTrue(state is DashboardScreenState.Content)

        val dashboard = (state as DashboardScreenState.Content).dashboard
        assertEquals(DryingVerdict.GOOD, dashboard.verdict.verdict)
        assertEquals("load-current", dashboard.activeLoadId)
        assertEquals(DashboardRecommendationAction.START_NOW, dashboard.recommendation.action)
        assertEquals(0f, dashboard.currentStatus.progress, 0.001f)
        assertTrue(dashboard.loadEstimate.estimatedDryingMinutes > 0)
        assertEquals("Backend data", dashboard.currentStatus.sourceLabel)
        assertTrue(dashboard.suitabilityScore > 0)
        assertNull(dashboard.rainRiskAlert)
    }

    @Test
    fun uiState_resolvesWasherNameForActiveLoad() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(),
                    weatherSnapshot = sampleWeather(),
                    sourceLabel = "Backend data",
                )
            ),
            washerDisplayNameProvider = { mapOf("main-washer" to "Lavarropas principal") },
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Content

        assertEquals("Lavarropas principal", state.dashboard.loadEstimate.washer)
        assertTrue(state.dashboard.loadEstimate.hasWasherLinked)
    }

    @Test
    fun uiState_doesNotExposeWasherUuidWhenWasherNameIsMissing() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(),
                    weatherSnapshot = sampleWeather(),
                    sourceLabel = "Backend data",
                )
            ),
            washerDisplayNameProvider = { emptyMap() },
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Content

        assertNull(state.dashboard.loadEstimate.washer)
        assertTrue(state.dashboard.loadEstimate.hasWasherLinked)
    }

    @Test
    fun uiState_recommendsIndoorOrWaitWhenForecastIsBad() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(status = LaundryLoadStatus.PLANNED),
                    weatherSnapshot = sampleWeather(rainProbabilityPercent = 95),
                    dryingPrediction = samplePrediction(verdict = DryingVerdict.BAD),
                    sourceLabel = "Backend prediction",
                )
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Content

        assertEquals(DashboardRecommendationAction.WAIT_OR_USE_INDOOR, state.dashboard.recommendation.action)
    }

    @Test
    fun uiState_recommendsMonitoringWhenLoadIsDrying() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(status = LaundryLoadStatus.DRYING),
                    weatherSnapshot = sampleWeather(source = WeatherDataSource.OPEN_METEO),
                    dryingPrediction = samplePrediction(verdict = DryingVerdict.CAUTION),
                    sourceLabel = "Backend prediction",
                )
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Content

        assertEquals(DashboardRecommendationAction.MONITOR_DRYING, state.dashboard.recommendation.action)
        assertEquals(222, state.dashboard.recommendation.estimatedDryingMinutes)
    }

    @Test
    fun uiState_usesElapsedDryingTimeForCurrentStatusProgress() {
        val dryingStartedAt = 1_767_638_400_000L
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(
                        status = LaundryLoadStatus.DRYING,
                        dryingStartedAtEpochMillis = dryingStartedAt,
                    ),
                    weatherSnapshot = sampleWeather(source = WeatherDataSource.OPEN_METEO),
                    dryingPrediction = samplePrediction(estimatedDryingMinutes = 120),
                    sourceLabel = "Backend prediction",
                )
            ),
            currentTimeMillis = { dryingStartedAt + 30L * 60_000L },
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Content

        assertEquals(0.25f, state.dashboard.currentStatus.progress, 0.001f)
    }

    @Test
    fun uiState_prefersDryingSnapshotEstimateForDryingProgress() {
        val dryingStartedAt = 1_767_638_400_000L
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(
                        status = LaundryLoadStatus.DRYING,
                        dryingStartedAtEpochMillis = dryingStartedAt,
                        dryingEstimatedMinutesAtStart = 120,
                    ),
                    weatherSnapshot = sampleWeather(source = WeatherDataSource.OPEN_METEO),
                    dryingPrediction = samplePrediction(estimatedDryingMinutes = 240),
                    sourceLabel = "Backend prediction",
                )
            ),
            currentTimeMillis = { dryingStartedAt + 60L * 60_000L },
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Content

        assertEquals(120, state.dashboard.currentStatus.estimatedDryingMinutes)
        assertEquals(120, state.dashboard.loadEstimate.estimatedDryingMinutes)
        assertEquals(0.5f, state.dashboard.currentStatus.progress, 0.001f)
    }

    @Test
    fun uiState_startsDryingProgressAtZeroWhenDryingTimestampIsMissing() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(status = LaundryLoadStatus.DRYING),
                    weatherSnapshot = sampleWeather(source = WeatherDataSource.OPEN_METEO),
                    dryingPrediction = samplePrediction(estimatedDryingMinutes = 120),
                    sourceLabel = "Backend prediction",
                )
            ),
            currentTimeMillis = { 1_767_638_400_000L + 30L * 60_000L },
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Content

        assertEquals(0f, state.dashboard.currentStatus.progress, 0.001f)
    }

    @Test
    fun uiState_exposesHighRainRiskAlertWhenForecastIsRisky() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(),
                    weatherSnapshot = sampleWeather(rainProbabilityPercent = 72),
                    sourceLabel = "Backend data",
                )
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Content
        val alert = state.dashboard.rainRiskAlert

        assertEquals(RainRiskSeverity.HIGH, alert?.severity)
        assertEquals(72, alert?.rainProbabilityPercent)
    }

    @Test
    fun uiState_usesBackendPredictionWhenRepositoryProvidesIt() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(dryingLocation = DryingLocation.BALCONY),
                    weatherSnapshot = sampleWeather(source = WeatherDataSource.OPEN_METEO),
                    dryingPrediction = samplePrediction(),
                    sourceLabel = "Backend prediction",
                )
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Content

        assertEquals(DryingVerdict.CAUTION, state.dashboard.verdict.verdict)
        assertEquals("Backend prediction reason.", state.dashboard.verdict.reason)
        assertEquals(222, state.dashboard.loadEstimate.estimatedDryingMinutes)
        assertEquals(DryingLocation.BALCONY, state.dashboard.loadEstimate.dryingLocation)
        assertEquals(EstimatedWashingCostLevel.MEDIUM, state.dashboard.loadEstimate.estimatedCost?.level)
        assertEquals("Open-Meteo", state.dashboard.sourceLabel)
    }

    @Test
    fun uiState_marksLocalFallbackSourceWhenWeatherIsMissing() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(),
                    weatherSnapshot = null,
                    sourceLabel = "Backend data",
                )
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Content

        assertEquals("Fallback local", state.dashboard.sourceLabel)
        assertEquals("Fallback local", state.dashboard.currentStatus.sourceLabel)
    }

    @Test
    fun uiState_marksStaleBackendWeatherAsBackendFallback() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(),
                    weatherSnapshot = sampleWeather(
                        source = WeatherDataSource.MOCK,
                        isStale = true,
                    ),
                    sourceLabel = "Backend data",
                )
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Content

        assertEquals("Backend fallback", state.dashboard.sourceLabel)
        assertEquals("Backend fallback", state.dashboard.currentStatus.sourceLabel)
    }

    @Test
    fun uiState_marksMetNoWeatherSourceWhenSecondaryProviderResponds() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = sampleLoad(),
                    weatherSnapshot = sampleWeather(source = WeatherDataSource.MET_NO),
                    sourceLabel = "Backend data",
                )
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Content

        assertEquals("MET Norway", state.dashboard.sourceLabel)
        assertEquals("MET Norway", state.dashboard.currentStatus.sourceLabel)
    }

    @Test
    fun uiState_exposesEmptyStateWhenBackendHasNoActiveLoad() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = null,
                    weatherSnapshot = null,
                    sourceLabel = "Backend online",
                )
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        assertEquals(DashboardScreenState.Empty(), viewModel.uiState.value)
    }

    @Test
    fun uiState_exposesWeatherRecommendationWhenBackendHasNoActiveLoadButHasWeather() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Success(
                    activeLoad = null,
                    weatherSnapshot = sampleWeather(source = WeatherDataSource.OPEN_METEO),
                    sourceLabel = "Backend weather",
                )
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as DashboardScreenState.Empty
        val recommendation = state.recommendation
        assertNotNull(recommendation)

        assertEquals(DryingLocation.PATIO, recommendation!!.dryingLocation)
        assertEquals("Open-Meteo", recommendation.sourceLabel)
        assertEquals("26 C", recommendation.weather.temperature)
        assertTrue(recommendation.estimatedDryingMinutes > 0)
        assertTrue(recommendation.suitabilityScore > 0)
    }

    @Test
    fun uiState_exposesBackendErrorState() {
        val viewModel = DashboardViewModel(
            dashboardRepository = FakeDashboardRepository(
                DashboardLoadResult.Error("Backend is unreachable")
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        assertEquals(
            DashboardScreenState.Error("Backend is unreachable"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun advanceActiveLoadStatus_updatesBackendAndRefreshesDashboard() {
        val repository = MutableDashboardRepository(sampleLoad(status = LaundryLoadStatus.PLANNED))
        val updater = FakeLaundryStatusUpdater { loadId, status ->
            repository.activeLoad = repository.activeLoad.copy(status = status)
            LaundryMutationResult.Success(repository.activeLoad)
        }
        val viewModel = DashboardViewModel(
            dashboardRepository = repository,
            laundryStatusUpdater = updater,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        viewModel.advanceActiveLoadStatus()

        val state = viewModel.uiState.value as DashboardScreenState.Content
        assertEquals("load-current", updater.lastLoadId)
        assertEquals(LaundryLoadStatus.WASHING, updater.lastStatus)
        assertEquals(LaundryLoadStatus.WASHING, state.dashboard.currentStatus.status)
    }

    @Test
    fun discardActiveLoad_marksLoadAsCancelledThroughBackend() {
        val repository = MutableDashboardRepository(sampleLoad(status = LaundryLoadStatus.DRYING))
        val updater = FakeLaundryStatusUpdater { loadId, status ->
            repository.activeLoad = repository.activeLoad.copy(status = status)
            LaundryMutationResult.Success(repository.activeLoad)
        }
        val viewModel = DashboardViewModel(
            dashboardRepository = repository,
            laundryStatusUpdater = updater,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        viewModel.discardActiveLoad()

        val state = viewModel.uiState.value as DashboardScreenState.Content
        assertEquals("load-current", updater.lastLoadId)
        assertEquals(LaundryLoadStatus.CANCELLED, updater.lastStatus)
        assertEquals(LaundryLoadStatus.CANCELLED, state.dashboard.currentStatus.status)
    }

    @Test
    fun screenState_definesLoadingEmptyAndErrorStates() {
        val error = DashboardScreenState.Error(message = "Offline")
        val states = listOf(
            DashboardScreenState.Loading,
            DashboardScreenState.Empty(),
            error,
        )

        assertEquals(3, states.size)
        assertEquals("Offline", error.message)
    }
}

private fun sampleWeather(
    rainProbabilityPercent: Int = 5,
    source: WeatherDataSource = WeatherDataSource.UNKNOWN,
    isStale: Boolean = false,
): WeatherSnapshot {
    return WeatherSnapshot(
        capturedAtEpochMillis = 1_767_638_400_000L,
        forecastForEpochMillis = 1_767_638_400_000L,
        location = WeatherLocation(
            id = "home",
            label = "Home patio",
            latitude = -34.6037,
            longitude = -58.3816,
        ),
        condition = com.tesis_pro.tenderapp.domain.model.WeatherCondition.CLEAR,
        temperatureCelsius = 26.0,
        humidityPercent = 45,
        windSpeedKph = 12.0,
        rainProbabilityPercent = rainProbabilityPercent,
        cloudCoverPercent = 10,
        source = source,
        isStale = isStale,
    )
}

private class FakeDashboardRepository(
    private val result: DashboardLoadResult,
) : DashboardRepository {
    override suspend fun loadDashboard(): DashboardLoadResult = result
}

private class MutableDashboardRepository(
    var activeLoad: LaundryLoad,
) : DashboardRepository {
    override suspend fun loadDashboard(): DashboardLoadResult {
        return DashboardLoadResult.Success(
            activeLoad = activeLoad,
            weatherSnapshot = sampleWeather(),
            sourceLabel = "Backend data",
        )
    }
}

private class FakeLaundryStatusUpdater(
    private val handler: (String, LaundryLoadStatus) -> LaundryMutationResult,
) : LaundryStatusUpdater {
    var lastLoadId: String? = null
        private set
    var lastStatus: LaundryLoadStatus? = null
        private set

    override suspend fun updateLaundryLoadStatus(
        loadId: String,
        status: LaundryLoadStatus,
    ): LaundryMutationResult {
        lastLoadId = loadId
        lastStatus = status
        return handler(loadId, status)
    }
}

private fun sampleLoad(
    dryingLocation: DryingLocation = DryingLocation.PATIO,
    status: LaundryLoadStatus = LaundryLoadStatus.PLANNED,
    dryingStartedAtEpochMillis: Long? = null,
    dryingEstimatedMinutesAtStart: Int? = null,
): LaundryLoad {
    return LaundryLoad(
        id = "load-current",
        washerId = "main-washer",
        clothingType = ClothingType.MIXED,
        washingProgram = WashingProgram.NORMAL,
        status = status,
        location = WeatherLocation(
            id = "home",
            label = "Home patio",
            latitude = null,
            longitude = null,
        ),
        dryingLocation = dryingLocation,
        createdAtEpochMillis = 1_767_638_400_000L,
        startedAtEpochMillis = null,
        completedAtEpochMillis = null,
        prediction = null,
        dryingStartedAtEpochMillis = dryingStartedAtEpochMillis,
        dryingEstimatedMinutesAtStart = dryingEstimatedMinutesAtStart,
    )
}

private fun samplePrediction(
    verdict: DryingVerdict = DryingVerdict.CAUTION,
    estimatedDryingMinutes: Int = 222,
): DryingPrediction {
    val weather = sampleWeather()
    return DryingPrediction(
        verdict = verdict,
        method = DryingMethod.OUTDOOR,
        estimatedDryingMinutes = estimatedDryingMinutes,
        recommendedHangAtEpochMillis = weather.forecastForEpochMillis,
        estimatedPickupAtEpochMillis = weather.forecastForEpochMillis + estimatedDryingMinutes * 60_000L,
        suitabilityScore = 61,
        reason = "Backend prediction reason.",
        weatherSnapshot = weather,
        estimatedCost = EstimatedWashingCost(
            amount = null,
            currency = null,
            level = EstimatedWashingCostLevel.MEDIUM,
            confidence = EstimatedWashingCostConfidence.LOW,
            estimatedEnergyKwh = 1.12,
            estimatedWaterLiters = 58.0,
        ),
    )
}
