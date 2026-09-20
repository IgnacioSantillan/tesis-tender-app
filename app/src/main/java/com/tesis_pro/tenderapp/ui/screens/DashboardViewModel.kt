package com.tesis_pro.tenderapp.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import com.tesis_pro.tenderapp.data.auth.BuildConfigSessionTokenProvider
import com.tesis_pro.tenderapp.data.auth.CompositeSessionTokenProvider
import com.tesis_pro.tenderapp.data.auth.SecureAuthSessionStore
import com.tesis_pro.tenderapp.data.remote.BackendApiClient
import com.tesis_pro.tenderapp.data.remote.RemoteLaundryDataSource
import com.tesis_pro.tenderapp.data.remote.RemotePredictionDataSource
import com.tesis_pro.tenderapp.data.remote.RemoteWeatherDataSource
import com.tesis_pro.tenderapp.data.remote.RemoteWasherDataSource
import com.tesis_pro.tenderapp.data.repository.BackendDashboardRepository
import com.tesis_pro.tenderapp.data.repository.BackendLaundryRepository
import com.tesis_pro.tenderapp.data.repository.BackendWasherRepository
import com.tesis_pro.tenderapp.data.repository.LocalHouseholdSettingsRepository
import com.tesis_pro.tenderapp.data.repository.LocalDryingPredictionRepository
import com.tesis_pro.tenderapp.data.repository.LaundryMutationResult
import com.tesis_pro.tenderapp.data.repository.LaundryStatusUpdater
import com.tesis_pro.tenderapp.data.repository.WasherRefreshResult
import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingHourlySlot
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.DryingMethod
import com.tesis_pro.tenderapp.domain.model.DryingPrediction
import com.tesis_pro.tenderapp.domain.model.DryingVerdict
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.model.WeatherDataSource
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.domain.model.WeatherCondition
import com.tesis_pro.tenderapp.domain.model.WeatherSnapshot
import com.tesis_pro.tenderapp.domain.repository.DashboardLoadResult
import com.tesis_pro.tenderapp.domain.repository.DashboardRepository
import com.tesis_pro.tenderapp.domain.repository.DryingPredictionRepository
import com.tesis_pro.tenderapp.domain.repository.HouseholdSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository,
    private val laundryStatusUpdater: LaundryStatusUpdater? = null,
    private val predictionRepository: DryingPredictionRepository = LocalDryingPredictionRepository(),
    householdSettingsRepository: HouseholdSettingsRepository = LocalHouseholdSettingsRepository(),
    private val washerDisplayNameProvider: suspend () -> Map<String, String> = { emptyMap() },
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
) : ViewModel() {
    private val weatherLocation = householdSettingsRepository.getWeatherLocation()
    private val defaultDryingLocation = householdSettingsRepository.getDefaultDryingLocation()
    private val mockWeather = WeatherSnapshot(
        capturedAtEpochMillis = MOCK_NOW_EPOCH_MILLIS,
        forecastForEpochMillis = MOCK_NOW_EPOCH_MILLIS,
        location = weatherLocation,
        condition = WeatherCondition.CLEAR,
        temperatureCelsius = 24.0,
        humidityPercent = 48,
        windSpeedKph = 18.0,
        rainProbabilityPercent = 8,
        cloudCoverPercent = 20,
        source = WeatherDataSource.LOCAL_FALLBACK,
        isStale = true,
    )

    private val _uiState = MutableStateFlow<DashboardScreenState>(DashboardScreenState.Loading)
    val uiState: StateFlow<DashboardScreenState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = DashboardScreenState.Loading
        coroutineScope.launch {
            _uiState.value = when (val result = dashboardRepository.loadDashboard()) {
                is DashboardLoadResult.Success -> {
                    val activeLoad = result.activeLoad
                    if (activeLoad == null) {
                        DashboardScreenState.Empty(
                            recommendation = result.weatherSnapshot?.let { weather ->
                                createEmptyRecommendationState(
                                    weather = weather,
                                    sourceLabel = result.sourceLabel.toDisplaySourceLabel(weather),
                                )
                            },
                        )
                    } else {
                        val weather = result.weatherSnapshot ?: mockWeather
                        val washerDisplayNames = loadWasherDisplayNames()
                        DashboardScreenState.Content(
                            createDashboardState(
                                load = activeLoad,
                                weather = weather,
                                backendPrediction = result.dryingPrediction,
                                sourceLabel = result.sourceLabel.toDisplaySourceLabel(weather),
                                washerDisplayNames = washerDisplayNames,
                            )
                        )
                    }
                }

                is DashboardLoadResult.Error -> DashboardScreenState.Error(result.message)
            }
        }
    }

    fun advanceActiveLoadStatus() {
        val currentState = _uiState.value as? DashboardScreenState.Content ?: return
        val nextStatus = currentState.dashboard.currentStatus.status.nextStatus() ?: return
        updateActiveLoadStatus(nextStatus)
    }

    fun discardActiveLoad() {
        val currentState = _uiState.value as? DashboardScreenState.Content ?: return
        if (!currentState.dashboard.currentStatus.status.isActive()) return
        updateActiveLoadStatus(LaundryLoadStatus.CANCELLED)
    }

    private fun updateActiveLoadStatus(status: LaundryLoadStatus) {
        val currentState = _uiState.value as? DashboardScreenState.Content ?: return
        val statusUpdater = laundryStatusUpdater ?: return

        _uiState.value = currentState.copy(
            isUpdatingStatus = true,
            statusUpdateError = null,
        )
        coroutineScope.launch {
            when (
                val result = statusUpdater.updateLaundryLoadStatus(
                    loadId = currentState.dashboard.activeLoadId,
                    status = status,
                )
            ) {
                is LaundryMutationResult.Success -> refresh()
                is LaundryMutationResult.Error -> {
                    _uiState.value = currentState.copy(
                        isUpdatingStatus = false,
                        statusUpdateError = result.message,
                    )
                }
            }
        }
    }

    override fun onCleared() {
        coroutineScope.cancel()
        super.onCleared()
    }

    private fun createDashboardState(
        load: LaundryLoad,
        weather: WeatherSnapshot,
        backendPrediction: DryingPrediction?,
        sourceLabel: String,
        washerDisplayNames: Map<String, String>,
    ): DashboardUiState {
        val prediction = backendPrediction ?: predictionRepository.calculateDryingPrediction(
            load = load,
            weather = weather,
            method = load.dryingLocation.toDryingMethod(),
        )
        val statusEstimateMinutes = load.dryingEstimatedMinutesAtStart
            ?.takeIf { load.status == LaundryLoadStatus.DRYING && it > 0 }
            ?: prediction.estimatedDryingMinutes

        return DashboardUiState(
            activeLoadId = load.id,
            verdict = WeatherVerdictUi(
                verdict = prediction.verdict,
                reason = prediction.reason,
            ),
            recommendation = ActionRecommendationUi(
                action = load.status.toRecommendationAction(prediction.verdict),
                estimatedDryingMinutes = statusEstimateMinutes,
            ),
            rainRiskAlert = createRainRiskAlert(weather),
            weather = weather.toWeatherMetricsUi(),
            loadEstimate = LoadEstimateUi(
                washer = load.washerId.toWasherDisplayName(washerDisplayNames),
                hasWasherLinked = !load.washerId.isNullOrBlank(),
                clothingType = load.clothingType,
                program = load.washingProgram,
                dryingLocation = load.dryingLocation,
                estimatedDryingMinutes = statusEstimateMinutes,
                estimatedCost = prediction.estimatedCost ?: load.estimatedWashingCost,
            ),
            currentStatus = CurrentStatusUi(
                status = load.status,
                estimatedDryingMinutes = statusEstimateMinutes,
                progress = load.calculateDryingProgress(statusEstimateMinutes),
                sourceLabel = sourceLabel,
                dryingStartedAtEpochMillis = load.dryingStartedAtEpochMillis,
                dryingEstimatedPickupAtEpochMillis = load.dryingEstimatedPickupAtEpochMillis,
            ),
            hangWindow = prediction.toHangWindowUi(),
            suitabilityScore = prediction.suitabilityScore,
            sourceLabel = sourceLabel,
        )
    }

    private fun createEmptyRecommendationState(
        weather: WeatherSnapshot,
        sourceLabel: String,
    ): DashboardEmptyRecommendationUiState {
        val referenceLoad = LaundryLoad(
            id = EMPTY_RECOMMENDATION_REFERENCE_LOAD_ID,
            washerId = null,
            clothingType = ClothingType.MIXED,
            washingProgram = WashingProgram.NORMAL,
            status = LaundryLoadStatus.PLANNED,
            location = weather.location,
            dryingLocation = defaultDryingLocation,
            createdAtEpochMillis = weather.capturedAtEpochMillis,
            startedAtEpochMillis = null,
            completedAtEpochMillis = null,
            prediction = null,
        )
        val prediction = predictionRepository.calculateDryingPrediction(
            load = referenceLoad,
            weather = weather,
            method = defaultDryingLocation.toDryingMethod(),
        )

        return DashboardEmptyRecommendationUiState(
            verdict = WeatherVerdictUi(
                verdict = prediction.verdict,
                reason = prediction.reason,
            ),
            rainRiskAlert = createRainRiskAlert(weather),
            weather = weather.toWeatherMetricsUi(),
            dryingLocation = defaultDryingLocation,
            estimatedDryingMinutes = prediction.estimatedDryingMinutes,
            suitabilityScore = prediction.suitabilityScore,
            sourceLabel = sourceLabel,
        )
    }

    private suspend fun loadWasherDisplayNames(): Map<String, String> {
        return runCatching { washerDisplayNameProvider() }
            .getOrDefault(emptyMap())
            .filterValues { it.isNotBlank() }
    }

    private fun createRainRiskAlert(weather: WeatherSnapshot): RainRiskAlertUi? {
        if (weather.rainProbabilityPercent < RAIN_ALERT_THRESHOLD_PERCENT) {
            return null
        }

        val severity = if (weather.rainProbabilityPercent >= RAIN_HIGH_RISK_THRESHOLD_PERCENT) {
            RainRiskSeverity.HIGH
        } else {
            RainRiskSeverity.WATCH
        }

        return RainRiskAlertUi(
            severity = severity,
            rainProbabilityPercent = weather.rainProbabilityPercent,
        )
    }

    private fun LaundryLoad.calculateDryingProgress(estimatedDryingMinutes: Int): Float {
        return when (status) {
            LaundryLoadStatus.PLANNED -> 0f
            LaundryLoadStatus.WASHING -> WASHING_PROGRESS_HINT
            LaundryLoadStatus.DRYING -> calculateElapsedDryingProgress(
                dryingStartedAtEpochMillis = dryingStartedAtEpochMillis,
                estimatedDryingMinutes = estimatedDryingMinutes,
            )
            LaundryLoadStatus.COMPLETED -> 1f
            LaundryLoadStatus.CANCELLED -> 0f
        }
    }

    private fun calculateElapsedDryingProgress(
        dryingStartedAtEpochMillis: Long?,
        estimatedDryingMinutes: Int,
    ): Float {
        val startedAt = dryingStartedAtEpochMillis ?: return 0f
        if (estimatedDryingMinutes <= 0) return 0f

        val elapsedMillis = (currentTimeMillis() - startedAt).coerceAtLeast(0L)
        val estimatedMillis = estimatedDryingMinutes * MILLIS_PER_MINUTE
        return (elapsedMillis.toDouble() / estimatedMillis.toDouble())
            .toFloat()
            .coerceIn(0f, MAX_ACTIVE_DRYING_PROGRESS)
    }

    companion object {
        const val MOCK_NOW_EPOCH_MILLIS = 1_767_638_400_000L
        private const val MILLIS_PER_MINUTE = 60_000L
        private const val WASHING_PROGRESS_HINT = 0.15f
        private const val MAX_ACTIVE_DRYING_PROGRESS = 0.99f
        private const val RAIN_ALERT_THRESHOLD_PERCENT = 35
        private const val RAIN_HIGH_RISK_THRESHOLD_PERCENT = 60
        private const val EMPTY_RECOMMENDATION_REFERENCE_LOAD_ID = "dashboard-empty-reference"

        fun createDefault(context: Context): DashboardViewModel {
            val appContext = context.applicationContext
            val api = BackendApiClient.create()
            val sessionTokenProvider = CompositeSessionTokenProvider(
                SecureAuthSessionStore(appContext),
                BuildConfigSessionTokenProvider(),
            )
            val laundryDataSource = RemoteLaundryDataSource(api)
            val washerRepository = BackendWasherRepository(
                washerDataSource = RemoteWasherDataSource(api),
                accessTokenProvider = sessionTokenProvider::getAccessToken,
            )
            return DashboardViewModel(
                dashboardRepository = BackendDashboardRepository(
                    api = api,
                    laundryDataSource = laundryDataSource,
                    weatherDataSource = RemoteWeatherDataSource(api),
                    predictionDataSource = RemotePredictionDataSource(BackendApiClient.createPredictionApi()),
                    householdSettingsRepository = LocalHouseholdSettingsRepository(appContext),
                    accessTokenProvider = sessionTokenProvider::getAccessToken,
                ),
                laundryStatusUpdater = BackendLaundryRepository(
                    laundryDataSource = laundryDataSource,
                    accessTokenProvider = sessionTokenProvider::getAccessToken,
                ),
                householdSettingsRepository = LocalHouseholdSettingsRepository(appContext),
                washerDisplayNameProvider = {
                    when (val result = washerRepository.refreshWashers()) {
                        is WasherRefreshResult.Success -> result.washers.toDisplayNameMap()
                        is WasherRefreshResult.Error -> washerRepository.currentWashers().toDisplayNameMap()
                    }
                },
            )
        }

        fun createDefaultDashboardRepository(context: Context): DashboardRepository {
            val api = BackendApiClient.create()
            val sessionTokenProvider = CompositeSessionTokenProvider(
                SecureAuthSessionStore(context),
                BuildConfigSessionTokenProvider(),
            )
            return BackendDashboardRepository(
                api = api,
                laundryDataSource = RemoteLaundryDataSource(api),
                weatherDataSource = RemoteWeatherDataSource(api),
                predictionDataSource = RemotePredictionDataSource(BackendApiClient.createPredictionApi()),
                householdSettingsRepository = LocalHouseholdSettingsRepository(context),
                accessTokenProvider = sessionTokenProvider::getAccessToken,
            )
        }
    }
}

    private fun WeatherSnapshot.toWeatherMetricsUi(): WeatherMetricsUi {
        return WeatherMetricsUi(
            temperature = "${temperatureCelsius.toInt()} C",
            humidity = "$humidityPercent%",
            wind = "${windSpeedKph.toInt()} km/h",
        )
    }

    private fun DryingPrediction.toHangWindowUi(): DryingHangWindowUi? {
        if (hourlySlots.isEmpty()) {
            return null
        }

        return DryingHangWindowUi(
            startEpochMillis = recommendedHangWindowStartEpochMillis,
            endEpochMillis = recommendedHangWindowEndEpochMillis,
            hourlySlots = hourlySlots.map { it.toForecastSlotUi() },
        )
    }

    private fun DryingHourlySlot.toForecastSlotUi(): DryingHourlyForecastSlotUi {
        return DryingHourlyForecastSlotUi(
            forecastForEpochMillis = forecastForEpochMillis,
            verdict = verdict,
            suitabilityScore = suitabilityScore,
            temperature = "${temperatureCelsius.toInt()} C",
            rainProbabilityPercent = rainProbabilityPercent,
        )
    }

private fun List<com.tesis_pro.tenderapp.domain.model.Washer>.toDisplayNameMap(): Map<String, String> {
    return associate { washer -> washer.id to washer.name.trim() }
        .filterValues { it.isNotBlank() }
}

private fun String?.toWasherDisplayName(washerDisplayNames: Map<String, String>): String? {
    val washerId = this?.trim().orEmpty()
    if (washerId.isEmpty()) return null
    return washerDisplayNames[washerId]
}

private fun String.toDisplaySourceLabel(weather: WeatherSnapshot): String {
    if (weather.isStale) {
        return when (weather.source) {
            WeatherDataSource.LOCAL_FALLBACK -> "Fallback local"
            else -> "Backend fallback"
        }
    }

    return when (weather.source) {
        WeatherDataSource.OPEN_METEO -> "Open-Meteo"
        WeatherDataSource.MET_NO -> "MET Norway"
        WeatherDataSource.MOCK -> "Mock backend"
        WeatherDataSource.LOCAL_FALLBACK -> "Fallback local"
        WeatherDataSource.UNKNOWN -> this
    }
}

private fun LaundryLoadStatus.toRecommendationAction(verdict: DryingVerdict): DashboardRecommendationAction {
    return when (this) {
        LaundryLoadStatus.PLANNED -> when (verdict) {
            DryingVerdict.GOOD -> DashboardRecommendationAction.START_NOW
            DryingVerdict.CAUTION -> DashboardRecommendationAction.START_WITH_CAUTION
            DryingVerdict.BAD -> DashboardRecommendationAction.WAIT_OR_USE_INDOOR
        }
        LaundryLoadStatus.WASHING -> when (verdict) {
            DryingVerdict.BAD -> DashboardRecommendationAction.WAIT_OR_USE_INDOOR
            DryingVerdict.GOOD,
            DryingVerdict.CAUTION,
            -> DashboardRecommendationAction.PREPARE_TO_HANG
        }
        LaundryLoadStatus.DRYING -> DashboardRecommendationAction.MONITOR_DRYING
        LaundryLoadStatus.COMPLETED,
        LaundryLoadStatus.CANCELLED,
        -> DashboardRecommendationAction.CHECK_DRYNESS
    }
}

private fun LaundryLoadStatus.nextStatus(): LaundryLoadStatus? {
    return when (this) {
        LaundryLoadStatus.PLANNED -> LaundryLoadStatus.WASHING
        LaundryLoadStatus.WASHING -> LaundryLoadStatus.DRYING
        LaundryLoadStatus.DRYING -> LaundryLoadStatus.COMPLETED
        LaundryLoadStatus.COMPLETED,
        LaundryLoadStatus.CANCELLED,
        -> null
    }
}

private fun LaundryLoadStatus.isActive(): Boolean {
    return this != LaundryLoadStatus.COMPLETED && this != LaundryLoadStatus.CANCELLED
}

private fun DryingLocation.toDryingMethod(): DryingMethod {
    return when (this) {
        DryingLocation.INDOOR, DryingLocation.LAUNDRY_ROOM -> DryingMethod.INDOOR
        DryingLocation.BALCONY, DryingLocation.OUTDOOR_LINE, DryingLocation.PATIO -> DryingMethod.OUTDOOR
    }
}
