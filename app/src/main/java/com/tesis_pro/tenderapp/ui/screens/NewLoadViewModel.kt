package com.tesis_pro.tenderapp.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import com.tesis_pro.tenderapp.data.auth.BuildConfigSessionTokenProvider
import com.tesis_pro.tenderapp.data.auth.CompositeSessionTokenProvider
import com.tesis_pro.tenderapp.data.auth.SecureAuthSessionStore
import com.tesis_pro.tenderapp.data.notification.LocalNotificationScheduler
import com.tesis_pro.tenderapp.data.notification.SharedPreferencesNotificationPreferencesRepository
import com.tesis_pro.tenderapp.data.remote.BackendApiClient
import com.tesis_pro.tenderapp.data.remote.CreateRemoteCompletionPlan
import com.tesis_pro.tenderapp.data.remote.CreateRemoteLaundryLoad
import com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType
import com.tesis_pro.tenderapp.data.remote.RemoteDataResult
import com.tesis_pro.tenderapp.data.remote.RemoteLaundryDataSource
import com.tesis_pro.tenderapp.data.remote.RemotePredictionDataSource
import com.tesis_pro.tenderapp.data.remote.RemoteWasherDataSource
import com.tesis_pro.tenderapp.data.remote.RemoteWeatherDataSource
import com.tesis_pro.tenderapp.data.remote.CreateRemoteDryingPrediction
import com.tesis_pro.tenderapp.data.repository.BackendLaundryRepository
import com.tesis_pro.tenderapp.data.repository.BackendWasherRepository
import com.tesis_pro.tenderapp.data.repository.LaundryMutationResult
import com.tesis_pro.tenderapp.data.repository.LocalDryingPredictionRepository
import com.tesis_pro.tenderapp.data.repository.LocalHouseholdSettingsRepository
import com.tesis_pro.tenderapp.data.repository.WasherRefreshResult
import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.DryingMethod
import com.tesis_pro.tenderapp.domain.model.DryingPrediction
import com.tesis_pro.tenderapp.domain.model.DryingVerdict
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCost
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.model.LoadSize
import com.tesis_pro.tenderapp.domain.model.LaundryCompletionPlan
import com.tesis_pro.tenderapp.domain.model.SpinSpeedRpm
import com.tesis_pro.tenderapp.domain.model.WeatherDataSource
import com.tesis_pro.tenderapp.domain.model.WeatherSnapshot
import com.tesis_pro.tenderapp.domain.model.Washer
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.domain.notification.NotificationScheduleResult
import com.tesis_pro.tenderapp.domain.notification.ScheduleIdealHangingReminderUseCase
import com.tesis_pro.tenderapp.domain.notification.SchedulePickupReminderUseCase
import com.tesis_pro.tenderapp.domain.repository.HouseholdSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

class NewLoadViewModel(
    private val laundryRepository: BackendLaundryRepository,
    private val washerRepository: BackendWasherRepository,
    private val weatherDataSource: RemoteWeatherDataSource? = null,
    private val predictionDataSource: RemotePredictionDataSource? = null,
    private val accessTokenProvider: (() -> String?)? = null,
    private val householdSettingsRepository: HouseholdSettingsRepository = LocalHouseholdSettingsRepository(),
    private val scheduleIdealHangingReminderUseCase: ScheduleIdealHangingReminderUseCase? = null,
    private val schedulePickupReminderUseCase: SchedulePickupReminderUseCase? = null,
    private val completionPlanLoader: (suspend (String, CreateRemoteCompletionPlan) -> RemoteDataResult<LaundryCompletionPlan>)? = null,
    private val nowProvider: () -> Long = System::currentTimeMillis,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        NewLoadScreenState(
            isLoadingWashers = true,
            selectedDryingLocation = householdSettingsRepository.getDefaultDryingLocation(),
            targetReadyAtEpochMillis = nowProvider() + DEFAULT_TARGET_OFFSET_MILLIS,
        )
    )
    val uiState: StateFlow<NewLoadScreenState> = _uiState.asStateFlow()
    private var completionPlanRequestVersion: Long = 0

    init {
        refreshWashers()
        refreshPredictionPreview()
        refreshWeatherPreview()
        refreshCompletionPlan()
    }

    fun refreshWashers() {
        _uiState.value = _uiState.value.copy(isLoadingWashers = true, result = null)
        coroutineScope.launch {
            when (val result = washerRepository.refreshWashers()) {
                is WasherRefreshResult.Success -> {
                    val selectedWasherId = _uiState.value.selectedWasherId
                        ?: result.washers.firstOrNull { it.isPrimary }?.id
                        ?: result.washers.firstOrNull()?.id
                    _uiState.value = _uiState.value.copy(
                        isLoadingWashers = false,
                        washers = result.washers,
                        selectedWasherId = selectedWasherId,
                    )
                    refreshCompletionPlan()
                }

                is WasherRefreshResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingWashers = false,
                        result = NewLoadResult.Error(
                            type = result.type,
                            fallbackMessage = result.message,
                        ),
                    )
                }
            }
        }
    }

    fun selectWasher(washerId: String?) {
        _uiState.value = _uiState.value.copy(selectedWasherId = washerId)
        refreshPredictionPreview()
        refreshCompletionPlan()
    }

    fun selectSpinRpm(spinRpm: SpinSpeedRpm) {
        _uiState.value = _uiState.value.copy(selectedSpinRpm = spinRpm)
        refreshPredictionPreview()
        refreshCompletionPlan()
    }

    fun selectLoadSize(loadSize: LoadSize) {
        _uiState.value = _uiState.value.copy(selectedLoadSize = loadSize)
        refreshPredictionPreview()
        refreshCompletionPlan()
    }

    fun selectClothingType(clothingType: ClothingType) {
        _uiState.value = _uiState.value.copy(selectedClothingType = clothingType)
        refreshPredictionPreview()
        refreshCompletionPlan()
    }

    fun selectWashingProgram(washingProgram: WashingProgram) {
        _uiState.value = _uiState.value.copy(selectedWashingProgram = washingProgram)
        refreshPredictionPreview()
    }

    fun selectDryingLocation(dryingLocation: DryingLocation) {
        _uiState.value = _uiState.value.copy(selectedDryingLocation = dryingLocation)
        refreshPredictionPreview()
        refreshCompletionPlan()
    }

    fun selectTargetReadyTime(hour: Int, minute: Int) {
        require(hour in 0..23) { "Hour must be between 0 and 23." }
        require(minute in 0..59) { "Minute must be between 0 and 59." }

        val now = Instant.ofEpochMilli(nowProvider()).atZone(ZoneId.systemDefault())
        var target = now
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)
            .withNano(0)
        if (!target.isAfter(now)) {
            target = target.plusDays(1)
        }
        _uiState.value = _uiState.value.copy(targetReadyAtEpochMillis = target.toInstant().toEpochMilli())
        refreshCompletionPlan()
    }

    fun refreshCompletionPlan() {
        val dataSource = predictionDataSource
        val loader = completionPlanLoader
        val tokenProvider = accessTokenProvider ?: return
        if (dataSource == null && loader == null) return

        val plannedStartAt = nowProvider()
        val currentState = _uiState.value
        val targetReadyAt = currentState.targetReadyAtEpochMillis.nextLocalOccurrenceAfter(plannedStartAt)
        val requestVersion = ++completionPlanRequestVersion
        _uiState.value = currentState.copy(
            targetReadyAtEpochMillis = targetReadyAt,
            isLoadingCompletionPlan = true,
            completionPlanError = null,
        )
        coroutineScope.launch {
            val request = CreateRemoteCompletionPlan(
                laundryLoadId = NEW_LOAD_PREVIEW_ID,
                clothingType = currentState.selectedClothingType,
                dryingMethod = currentState.selectedDryingLocation.toDryingMethod(),
                locationId = householdSettingsRepository.getWeatherLocation().id,
                dryingLocation = currentState.selectedDryingLocation,
                spinRpm = currentState.resolvedSpinRpm,
                loadSize = currentState.selectedLoadSize,
                washerEnergyLabel = currentState.selectedWasher?.energyLabel,
                washerCapacityKg = currentState.selectedWasher?.capacityKg,
                waterUsageLiters = currentState.selectedWasher?.waterUsageLiters,
                plannedStartAtEpochMillis = plannedStartAt,
                targetReadyAtEpochMillis = targetReadyAt,
            )
            val result = loader?.invoke(tokenProvider().orEmpty(), request)
                ?: dataSource!!.calculateCompletionPlan(tokenProvider().orEmpty(), request)
            if (requestVersion != completionPlanRequestVersion) return@launch

            _uiState.value = when (result) {
                is RemoteDataResult.Success -> _uiState.value.copy(
                    isLoadingCompletionPlan = false,
                    backendCompletionPlan = result.data,
                    completionPlanError = null,
                )
                is RemoteDataResult.Error -> _uiState.value.copy(
                    isLoadingCompletionPlan = false,
                    backendCompletionPlan = null,
                    completionPlanError = result.message,
                )
            }
        }
    }

    fun refreshPredictionPreview() {
        val dataSource = predictionDataSource ?: return
        val tokenProvider = accessTokenProvider ?: return
        val currentState = _uiState.value
        _uiState.value = currentState.copy(isLoadingPredictionPreview = true, predictionPreviewError = null)
        coroutineScope.launch {
            val locationId = householdSettingsRepository.getWeatherLocation().id
            val request = CreateRemoteDryingPrediction(
                laundryLoadId = NEW_LOAD_PREVIEW_ID,
                clothingType = currentState.selectedClothingType,
                washingProgram = currentState.selectedWashingProgram,
                dryingMethod = currentState.selectedDryingLocation.toDryingMethod(),
                locationId = locationId,
                dryingLocation = currentState.selectedDryingLocation,
                spinRpm = currentState.resolvedSpinRpm,
                loadSize = currentState.selectedLoadSize,
                washerEnergyLabel = currentState.selectedWasher?.energyLabel,
                washerCapacityKg = currentState.selectedWasher?.capacityKg,
                waterUsageLiters = currentState.selectedWasher?.waterUsageLiters,
            )
            _uiState.value = when (val result = dataSource.calculateDryingPrediction(tokenProvider().orEmpty(), request)) {
                is RemoteDataResult.Success -> {
                    if (!_uiState.value.matchesPredictionSelection(currentState)) return@launch
                    _uiState.value.copy(
                        isLoadingPredictionPreview = false,
                        backendPredictionPreview = result.data,
                        weatherSnapshot = result.data.weatherSnapshot,
                        predictionPreviewError = null,
                    )
                }
                is RemoteDataResult.Error -> {
                    if (!_uiState.value.matchesPredictionSelection(currentState)) return@launch
                    _uiState.value.copy(
                        isLoadingPredictionPreview = false,
                        predictionPreviewError = result.message,
                    )
                }
            }
        }
    }

    fun refreshWeatherPreview() {
        val dataSource = weatherDataSource ?: return
        val tokenProvider = accessTokenProvider ?: return
        _uiState.value = _uiState.value.copy(isLoadingWeatherPreview = true, weatherPreviewError = null)
        coroutineScope.launch {
            val locationId = householdSettingsRepository.getWeatherLocation().id
            _uiState.value = when (val result = dataSource.currentWeather(tokenProvider().orEmpty(), locationId)) {
                is RemoteDataResult.Success -> _uiState.value.copy(
                    isLoadingWeatherPreview = false,
                    weatherSnapshot = result.data,
                    weatherPreviewError = null,
                )
                is RemoteDataResult.Error -> _uiState.value.copy(
                    isLoadingWeatherPreview = false,
                    weatherPreviewError = result.message,
                )
            }
        }
    }

    fun createDefaultLoad() {
        if (_uiState.value.isSubmitting) return

        _uiState.value = _uiState.value.copy(
            isSubmitting = true,
            result = null,
        )
        coroutineScope.launch {
            val currentState = _uiState.value
            _uiState.value = when (val result = laundryRepository.createLaundryLoad(createDefaultRequest())) {
                is LaundryMutationResult.Success -> {
                    val reminderResult = scheduleIdealHangingReminderUseCase?.schedule(
                        load = result.load,
                        washingProgram = currentState.selectedWashingProgram,
                    )
                    val pickupReminderResult = schedulePickupReminderUseCase?.schedule(
                        load = result.load,
                        washingProgram = currentState.selectedWashingProgram,
                        clothingType = currentState.selectedClothingType,
                    )
                    currentState.copy(
                        isSubmitting = false,
                        result = NewLoadResult.Success(
                            idealHangingReminderScheduled = reminderResult is NotificationScheduleResult.Scheduled,
                            pickupReminderScheduled = pickupReminderResult is NotificationScheduleResult.Scheduled,
                        ),
                    )
                }

                is LaundryMutationResult.Error -> currentState.copy(
                    isSubmitting = false,
                    result = NewLoadResult.Error(
                        type = result.type,
                        fallbackMessage = result.message,
                    ),
                )
            }
        }
    }

    override fun onCleared() {
        coroutineScope.cancel()
        super.onCleared()
    }

    private fun createDefaultRequest(): CreateRemoteLaundryLoad {
        val currentState = _uiState.value
        return CreateRemoteLaundryLoad(
            washerId = currentState.selectedWasherId,
            clothingType = currentState.selectedClothingType,
            washingProgram = currentState.selectedWashingProgram,
            locationId = householdSettingsRepository.getWeatherLocation().id,
            dryingLocation = currentState.selectedDryingLocation,
            spinRpm = currentState.resolvedSpinRpm,
            loadSize = currentState.selectedLoadSize,
        )
    }

    companion object {
        fun createDefault(context: Context): NewLoadViewModel {
            val api = BackendApiClient.create()
            val sessionTokenProvider = CompositeSessionTokenProvider(
                SecureAuthSessionStore(context),
                BuildConfigSessionTokenProvider(),
            )
            val repository = BackendLaundryRepository(
                laundryDataSource = RemoteLaundryDataSource(api),
                accessTokenProvider = sessionTokenProvider::getAccessToken,
            )
            val washerRepository = BackendWasherRepository(
                washerDataSource = RemoteWasherDataSource(api),
                accessTokenProvider = sessionTokenProvider::getAccessToken,
            )
            val notificationPreferencesRepository =
                SharedPreferencesNotificationPreferencesRepository(context)
            val notificationScheduler = LocalNotificationScheduler(
                preferencesProvider = notificationPreferencesRepository::currentPreferences,
            )
            return NewLoadViewModel(
                laundryRepository = repository,
                washerRepository = washerRepository,
                weatherDataSource = RemoteWeatherDataSource(api),
                predictionDataSource = RemotePredictionDataSource(BackendApiClient.createPredictionApi()),
                accessTokenProvider = sessionTokenProvider::getAccessToken,
                householdSettingsRepository = LocalHouseholdSettingsRepository(context),
                scheduleIdealHangingReminderUseCase = ScheduleIdealHangingReminderUseCase(notificationScheduler),
                schedulePickupReminderUseCase = SchedulePickupReminderUseCase(notificationScheduler),
            )
        }

        private const val NEW_LOAD_PREVIEW_ID = "new-load-preview"
        private const val DEFAULT_TARGET_OFFSET_MILLIS = 6L * 60L * 60L * 1_000L
    }
}

private fun Long.nextLocalOccurrenceAfter(nowEpochMillis: Long): Long {
    if (this > nowEpochMillis) return this

    val zone = ZoneId.systemDefault()
    val selectedTime = Instant.ofEpochMilli(this).atZone(zone).toLocalTime()
    val now = Instant.ofEpochMilli(nowEpochMillis).atZone(zone)
    var candidate = now
        .withHour(selectedTime.hour)
        .withMinute(selectedTime.minute)
        .withSecond(0)
        .withNano(0)
    if (!candidate.isAfter(now)) {
        candidate = candidate.plusDays(1)
    }
    return candidate.toInstant().toEpochMilli()
}

data class NewLoadScreenState(
    val isLoadingWashers: Boolean = false,
    val isSubmitting: Boolean = false,
    val washers: List<Washer> = emptyList(),
    val selectedWasherId: String? = null,
    val selectedClothingType: ClothingType = ClothingType.MIXED,
    val selectedWashingProgram: WashingProgram = WashingProgram.ECO,
    val selectedDryingLocation: DryingLocation = DryingLocation.PATIO,
    val selectedSpinRpm: SpinSpeedRpm? = null,
    val selectedLoadSize: LoadSize = LoadSize.MEDIUM,
    val isLoadingWeatherPreview: Boolean = false,
    val isLoadingPredictionPreview: Boolean = false,
    val backendPredictionPreview: DryingPrediction? = null,
    val weatherSnapshot: WeatherSnapshot? = null,
    val predictionPreviewError: String? = null,
    val weatherPreviewError: String? = null,
    val targetReadyAtEpochMillis: Long = 0L,
    val isLoadingCompletionPlan: Boolean = false,
    val backendCompletionPlan: LaundryCompletionPlan? = null,
    val completionPlanError: String? = null,
    val result: NewLoadResult? = null,
) {
    val selectedWasher: Washer?
        get() = washers.firstOrNull { it.id == selectedWasherId }

    val resolvedSpinRpm: SpinSpeedRpm
        get() = selectedSpinRpm ?: selectedWasher?.defaultSpinRpm ?: SpinSpeedRpm.RPM_1200

    val decisionPreview: NewLoadDecisionPreview
        get() = backendPredictionPreview?.let { prediction ->
            NewLoadDecisionPreview.fromBackendPrediction(prediction)
        } ?: weatherSnapshot?.let { weather ->
            NewLoadDecisionPreview.fromWeather(
                clothingType = selectedClothingType,
                washingProgram = selectedWashingProgram,
                dryingLocation = selectedDryingLocation,
                spinRpm = resolvedSpinRpm,
                loadSize = selectedLoadSize,
                weather = weather,
            )
        } ?: NewLoadDecisionPreview.fromSelectionFallback(
            clothingType = selectedClothingType,
            washingProgram = selectedWashingProgram,
            dryingLocation = selectedDryingLocation,
        )
}

data class NewLoadDecisionPreview(
    val profile: NewLoadDecisionProfile,
    val estimatedDryingMinutes: Int,
    val selectionScore: Int,
    val source: NewLoadDecisionSource,
    val temperatureCelsius: Int?,
    val estimatedCost: EstimatedWashingCost? = null,
) {
    companion object {
        private val predictionRepository = LocalDryingPredictionRepository()

        fun fromBackendPrediction(prediction: DryingPrediction): NewLoadDecisionPreview {
            return NewLoadDecisionPreview(
                profile = prediction.verdict.toDecisionProfile(),
                estimatedDryingMinutes = prediction.estimatedDryingMinutes,
                selectionScore = prediction.suitabilityScore,
                source = NewLoadDecisionSource.BACKEND_PREDICTION,
                temperatureCelsius = prediction.weatherSnapshot.temperatureCelsius.toInt(),
                estimatedCost = prediction.estimatedCost,
            )
        }

        fun fromWeather(
            clothingType: ClothingType,
            washingProgram: WashingProgram,
            dryingLocation: DryingLocation,
            spinRpm: SpinSpeedRpm,
            loadSize: LoadSize,
            weather: WeatherSnapshot,
        ): NewLoadDecisionPreview {
            val prediction = predictionRepository.calculateDryingPrediction(
                load = LaundryLoad(
                    id = "new-load-preview",
                    washerId = null,
                    clothingType = clothingType,
                    washingProgram = washingProgram,
                    status = LaundryLoadStatus.PLANNED,
                    location = weather.location,
                    dryingLocation = dryingLocation,
                    createdAtEpochMillis = weather.capturedAtEpochMillis,
                    startedAtEpochMillis = null,
                    completedAtEpochMillis = null,
                    prediction = null,
                    spinRpm = spinRpm,
                    loadSize = loadSize,
                ),
                weather = weather,
                method = dryingLocation.toDryingMethod(),
            )

            return NewLoadDecisionPreview(
                profile = prediction.verdict.toDecisionProfile(),
                estimatedDryingMinutes = prediction.estimatedDryingMinutes,
                selectionScore = prediction.suitabilityScore,
                source = NewLoadDecisionSource.fromWeather(weather),
                temperatureCelsius = weather.temperatureCelsius.toInt(),
                estimatedCost = prediction.estimatedCost,
            )
        }

        fun fromSelectionFallback(
            clothingType: ClothingType,
            washingProgram: WashingProgram,
            dryingLocation: DryingLocation,
        ): NewLoadDecisionPreview {
            val baseMinutes = when (clothingType) {
                ClothingType.LIGHT_CLOTHES -> 120
                ClothingType.DELICATES -> 150
                ClothingType.MIXED -> 180
                ClothingType.HEAVY_CLOTHES -> 260
                ClothingType.BEDDING -> 300
            }
            val locationMultiplier = when (dryingLocation) {
                DryingLocation.BALCONY,
                DryingLocation.OUTDOOR_LINE,
                DryingLocation.PATIO,
                -> 1.0
                DryingLocation.INDOOR,
                DryingLocation.LAUNDRY_ROOM,
                -> 1.35
            }
            val programMultiplier = when (washingProgram) {
                WashingProgram.QUICK -> 1.15
                WashingProgram.NORMAL -> 1.0
                WashingProgram.ECO -> 1.05
                WashingProgram.DELICATE -> 1.2
            }
            val estimatedMinutes = (baseMinutes * locationMultiplier * programMultiplier).toInt()
            val score = (
                82 +
                    dryingLocation.scoreAdjustment() +
                    clothingType.scoreAdjustment() +
                    washingProgram.scoreAdjustment()
                ).coerceIn(0, 100)
            val profile = when {
                dryingLocation == DryingLocation.INDOOR ||
                    dryingLocation == DryingLocation.LAUNDRY_ROOM -> NewLoadDecisionProfile.INDOOR_STABLE
                score >= 76 -> NewLoadDecisionProfile.RECOMMENDED
                else -> NewLoadDecisionProfile.MONITOR
            }

            return NewLoadDecisionPreview(
                profile = profile,
                estimatedDryingMinutes = estimatedMinutes,
                selectionScore = score,
                source = NewLoadDecisionSource.SELECTION_FALLBACK,
                temperatureCelsius = null,
            )
        }
    }
}

enum class NewLoadDecisionProfile {
    RECOMMENDED,
    MONITOR,
    WAIT_OR_INDOOR,
    INDOOR_STABLE,
}

enum class NewLoadDecisionSource {
    BACKEND_PREDICTION,
    OPEN_METEO,
    MET_NO,
    BACKEND_MOCK,
    LOCAL_FALLBACK,
    SELECTION_FALLBACK,
    UNKNOWN;

    companion object {
        fun fromWeather(weather: WeatherSnapshot): NewLoadDecisionSource {
            return when (weather.source) {
                WeatherDataSource.OPEN_METEO -> OPEN_METEO
                WeatherDataSource.MET_NO -> MET_NO
                WeatherDataSource.MOCK -> BACKEND_MOCK
                WeatherDataSource.LOCAL_FALLBACK -> LOCAL_FALLBACK
                WeatherDataSource.UNKNOWN -> UNKNOWN
            }
        }
    }
}

private fun DryingVerdict.toDecisionProfile(): NewLoadDecisionProfile {
    return when (this) {
        DryingVerdict.GOOD -> NewLoadDecisionProfile.RECOMMENDED
        DryingVerdict.CAUTION -> NewLoadDecisionProfile.MONITOR
        DryingVerdict.BAD -> NewLoadDecisionProfile.WAIT_OR_INDOOR
    }
}

private fun DryingLocation.scoreAdjustment(): Int {
    return when (this) {
        DryingLocation.OUTDOOR_LINE,
        DryingLocation.PATIO,
        -> 4
        DryingLocation.BALCONY -> 1
        DryingLocation.LAUNDRY_ROOM -> -8
        DryingLocation.INDOOR -> -12
    }
}

private fun ClothingType.scoreAdjustment(): Int {
    return when (this) {
        ClothingType.LIGHT_CLOTHES -> 6
        ClothingType.DELICATES -> 2
        ClothingType.MIXED -> 0
        ClothingType.HEAVY_CLOTHES -> -8
        ClothingType.BEDDING -> -12
    }
}

private fun WashingProgram.scoreAdjustment(): Int {
    return when (this) {
        WashingProgram.ECO -> 4
        WashingProgram.NORMAL -> 2
        WashingProgram.QUICK -> -3
        WashingProgram.DELICATE -> -4
    }
}

private fun DryingLocation.toDryingMethod(): DryingMethod {
    return when (this) {
        DryingLocation.INDOOR,
        DryingLocation.LAUNDRY_ROOM,
        -> DryingMethod.INDOOR
        DryingLocation.BALCONY,
        DryingLocation.OUTDOOR_LINE,
        DryingLocation.PATIO,
        -> DryingMethod.OUTDOOR
    }
}

private fun NewLoadScreenState.matchesPredictionSelection(other: NewLoadScreenState): Boolean {
    return selectedClothingType == other.selectedClothingType &&
        selectedWashingProgram == other.selectedWashingProgram &&
        selectedDryingLocation == other.selectedDryingLocation &&
        selectedWasherId == other.selectedWasherId &&
        resolvedSpinRpm == other.resolvedSpinRpm &&
        selectedLoadSize == other.selectedLoadSize
}

sealed interface NewLoadResult {
    data class Success(
        val idealHangingReminderScheduled: Boolean,
        val pickupReminderScheduled: Boolean,
    ) : NewLoadResult

    data class Error(
        val type: RemoteDataErrorType,
        val fallbackMessage: String,
    ) : NewLoadResult
}
