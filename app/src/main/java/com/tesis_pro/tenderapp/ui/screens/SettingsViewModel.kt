package com.tesis_pro.tenderapp.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import com.tesis_pro.tenderapp.data.auth.BuildConfigSessionTokenProvider
import com.tesis_pro.tenderapp.data.auth.CompositeSessionTokenProvider
import com.tesis_pro.tenderapp.data.auth.SecureAuthSessionStore
import com.tesis_pro.tenderapp.data.notification.InMemoryNotificationPreferencesRepository
import com.tesis_pro.tenderapp.data.notification.SharedPreferencesNotificationPreferencesRepository
import com.tesis_pro.tenderapp.data.remote.BackendApiClient
import com.tesis_pro.tenderapp.data.remote.CreateRemoteWasher
import com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType
import com.tesis_pro.tenderapp.data.remote.RemoteUserLocationDataSource
import com.tesis_pro.tenderapp.data.remote.RemoteWasherDataSource
import com.tesis_pro.tenderapp.data.repository.BackendUserLocationRepository
import com.tesis_pro.tenderapp.data.repository.BackendWasherRepository
import com.tesis_pro.tenderapp.data.repository.LocalHouseholdSettingsRepository
import com.tesis_pro.tenderapp.data.repository.WasherMutationResult
import com.tesis_pro.tenderapp.data.repository.WasherRefreshResult
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.SpinSpeedRpm
import com.tesis_pro.tenderapp.domain.model.Washer
import com.tesis_pro.tenderapp.domain.model.WasherEnergyLabel
import com.tesis_pro.tenderapp.domain.model.WasherType
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.notification.NotificationPreferences
import com.tesis_pro.tenderapp.domain.notification.NotificationPreferencesRepository
import com.tesis_pro.tenderapp.domain.repository.HouseholdSettingsRepository
import com.tesis_pro.tenderapp.domain.repository.UserLocationRepository
import com.tesis_pro.tenderapp.domain.repository.UserLocationSaveResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val washerRepository: BackendWasherRepository,
    private val householdSettingsRepository: HouseholdSettingsRepository = LocalHouseholdSettingsRepository(),
    private val userLocationRepository: UserLocationRepository? = null,
    private val notificationPreferencesRepository: NotificationPreferencesRepository =
        InMemoryNotificationPreferencesRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        SettingsScreenState(
            isLoadingWashers = true,
            notificationPreferences = notificationPreferencesRepository.currentPreferences(),
            householdForm = SettingsHouseholdFormState.from(
                location = householdSettingsRepository.getWeatherLocation(),
                defaultDryingLocation = householdSettingsRepository.getDefaultDryingLocation(),
            ),
        )
    )
    val uiState: StateFlow<SettingsScreenState> = _uiState.asStateFlow()

    init {
        refreshWashers()
    }

    fun refreshWashers() {
        _uiState.value = _uiState.value.copy(isLoadingWashers = true, washerMessage = null)
        coroutineScope.launch {
            _uiState.value = when (val result = washerRepository.refreshWashers()) {
                is WasherRefreshResult.Success -> _uiState.value.copy(
                    isLoadingWashers = false,
                    washers = result.washers,
                )

                is WasherRefreshResult.Error -> _uiState.value.copy(
                    isLoadingWashers = false,
                    washerMessage = SettingsWasherMessage.Error(result.type, result.message),
                )
            }
        }
    }

    fun createWasher() {
        if (_uiState.value.isCreatingWasher) return
        val request = _uiState.value.washerForm.toCreateRemoteWasher(
            makePrimaryByDefault = _uiState.value.washers.none { it.isPrimary },
        ) ?: return

        _uiState.value = _uiState.value.copy(isCreatingWasher = true, washerMessage = null)
        coroutineScope.launch {
            _uiState.value = when (val result = washerRepository.createWasher(request)) {
                is WasherMutationResult.Success -> _uiState.value.copy(
                    isCreatingWasher = false,
                    washers = washerRepository.currentWashers(),
                    washerForm = SettingsWasherFormState.defaultFor(
                        isPrimary = washerRepository.currentWashers().none { it.isPrimary },
                    ),
                    washerMessage = SettingsWasherMessage.Created(result.washer.name),
                )

                is WasherMutationResult.Error -> _uiState.value.copy(
                    isCreatingWasher = false,
                    washerMessage = SettingsWasherMessage.Error(result.type, result.message),
                )
            }
        }
    }

    fun startEditingWasher(washer: Washer) {
        _uiState.value = _uiState.value.copy(
            editingWasherId = washer.id,
            washerForm = SettingsWasherFormState.from(washer),
            washerMessage = null,
        )
    }

    fun cancelWasherEdit() {
        _uiState.value = _uiState.value.copy(
            editingWasherId = null,
            washerForm = SettingsWasherFormState.defaultFor(
                isPrimary = _uiState.value.washers.none { it.isPrimary },
            ),
            washerMessage = null,
        )
    }

    fun saveWasherEdit() {
        val editingWasherId = _uiState.value.editingWasherId ?: return
        if (_uiState.value.isSavingWasherEdit) return
        val request = _uiState.value.washerForm.toCreateRemoteWasher(
            makePrimaryByDefault = false,
        ) ?: return

        _uiState.value = _uiState.value.copy(isSavingWasherEdit = true, washerMessage = null)
        coroutineScope.launch {
            _uiState.value = when (val result = washerRepository.updateWasher(editingWasherId, request)) {
                is WasherMutationResult.Success -> _uiState.value.copy(
                    isSavingWasherEdit = false,
                    editingWasherId = null,
                    washers = washerRepository.currentWashers(),
                    washerForm = SettingsWasherFormState.defaultFor(
                        isPrimary = washerRepository.currentWashers().none { it.isPrimary },
                    ),
                    washerMessage = SettingsWasherMessage.Updated(result.washer.name),
                )

                is WasherMutationResult.Error -> _uiState.value.copy(
                    isSavingWasherEdit = false,
                    washerMessage = SettingsWasherMessage.Error(result.type, result.message),
                )
            }
        }
    }

    fun retireWasher(washer: Washer) {
        if (_uiState.value.isRetiringWasherId != null) return
        _uiState.value = _uiState.value.copy(
            isRetiringWasherId = washer.id,
            washerMessage = null,
        )
        coroutineScope.launch {
            _uiState.value = when (val result = washerRepository.retireWasher(washer.id)) {
                is WasherMutationResult.Success -> _uiState.value.copy(
                    isRetiringWasherId = null,
                    washers = washerRepository.currentWashers(),
                    washerForm = if (_uiState.value.editingWasherId == washer.id) {
                        SettingsWasherFormState.defaultFor(
                            isPrimary = washerRepository.currentWashers().none { it.isPrimary },
                        )
                    } else {
                        _uiState.value.washerForm.copy(
                            isPrimary = washerRepository.currentWashers().none { it.isPrimary },
                        )
                    },
                    editingWasherId = _uiState.value.editingWasherId?.takeUnless { it == washer.id },
                    washerMessage = SettingsWasherMessage.Retired(washer.name),
                )

                is WasherMutationResult.Error -> _uiState.value.copy(
                    isRetiringWasherId = null,
                    washerMessage = SettingsWasherMessage.Error(result.type, result.message),
                )
            }
        }
    }


    fun toggleNotificationPreference(
        preference: SettingsNotificationPreference,
        enabled: Boolean,
    ) {
        val current = _uiState.value.notificationPreferences
        val updated = when (preference) {
            SettingsNotificationPreference.ALL -> current.copy(notificationsEnabled = enabled)
            SettingsNotificationPreference.IDEAL_HANGING_TIME -> current.copy(idealHangingTimeEnabled = enabled)
            SettingsNotificationPreference.DRYING_COMPLETE -> current.copy(dryingCompleteEnabled = enabled)
            SettingsNotificationPreference.RAIN_RISK -> current.copy(rainRiskEnabled = enabled)
        }
        _uiState.value = _uiState.value.copy(notificationPreferences = updated)
        notificationPreferencesRepository.updatePreferences(updated)
    }

    fun setQuietHoursEnabled(enabled: Boolean) {
        updateNotificationPreferences {
            copy(quietHoursEnabled = enabled)
        }
    }

    fun setQuietHoursStart(value: String) {
        updateNotificationPreferences {
            copy(quietHoursStart = value)
        }
    }

    fun setQuietHoursEnd(value: String) {
        updateNotificationPreferences {
            copy(quietHoursEnd = value)
        }
    }

    private fun updateNotificationPreferences(
        transform: NotificationPreferences.() -> NotificationPreferences,
    ) {
        val updated = _uiState.value.notificationPreferences.transform()
        _uiState.value = _uiState.value.copy(notificationPreferences = updated)
        notificationPreferencesRepository.updatePreferences(updated)
    }

    fun onWasherNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(
            washerForm = _uiState.value.washerForm.copy(name = value),
            washerMessage = null,
        )
    }

    fun onWasherTypeChanged(value: WasherType) {
        _uiState.value = _uiState.value.copy(
            washerForm = _uiState.value.washerForm.copy(type = value),
            washerMessage = null,
        )
    }

    fun onWasherCapacityChanged(value: String) {
        _uiState.value = _uiState.value.copy(
            washerForm = _uiState.value.washerForm.copy(capacityKg = value),
            washerMessage = null,
        )
    }

    fun onWasherEnergyLabelChanged(value: WasherEnergyLabel?) {
        _uiState.value = _uiState.value.copy(
            washerForm = _uiState.value.washerForm.copy(energyLabel = value),
            washerMessage = null,
        )
    }

    fun onWasherWaterUsageChanged(value: String) {
        _uiState.value = _uiState.value.copy(
            washerForm = _uiState.value.washerForm.copy(waterUsageLiters = value),
            washerMessage = null,
        )
    }

    fun onWasherDefaultSpinRpmChanged(value: SpinSpeedRpm?) {
        _uiState.value = _uiState.value.copy(
            washerForm = _uiState.value.washerForm.copy(defaultSpinRpm = value),
            washerMessage = null,
        )
    }

    fun onWasherPrimaryChanged(value: Boolean) {
        _uiState.value = _uiState.value.copy(
            washerForm = _uiState.value.washerForm.copy(isPrimary = value),
            washerMessage = null,
        )
    }

    fun onHouseholdLocationLabelChanged(value: String) {
        _uiState.value = _uiState.value.copy(
            householdForm = _uiState.value.householdForm.copy(locationLabel = value),
            householdMessage = null,
        )
    }

    fun onDefaultDryingLocationChanged(value: DryingLocation) {
        _uiState.value = _uiState.value.copy(
            householdForm = _uiState.value.householdForm.copy(defaultDryingLocation = value),
            householdMessage = null,
        )
    }

    fun onCurrentLocationSelected(
        latitude: Double,
        longitude: Double,
        label: String,
    ) {
        val currentLabel = _uiState.value.householdForm.locationLabel.trim()
        _uiState.value = _uiState.value.copy(
            householdForm = _uiState.value.householdForm.copy(
                locationLabel = currentLabel.ifBlank { label },
                latitude = latitude,
                longitude = longitude,
            ),
            householdMessage = SettingsHouseholdMessage.CurrentLocationSelected,
        )
    }

    fun onCurrentLocationUnavailable() {
        _uiState.value = _uiState.value.copy(
            householdMessage = SettingsHouseholdMessage.CurrentLocationUnavailable,
        )
    }

    fun onCurrentLocationPermissionDenied() {
        _uiState.value = _uiState.value.copy(
            householdMessage = SettingsHouseholdMessage.LocationPermissionDenied,
        )
    }

    fun saveHouseholdDefaults() {
        val form = _uiState.value.householdForm
        if (!form.canSubmit) return
        val weatherLocation = form.toWeatherLocation()
        _uiState.value = _uiState.value.copy(householdMessage = null)
        coroutineScope.launch {
            val remoteResult = userLocationRepository?.saveHouseholdLocation(weatherLocation)
                ?: UserLocationSaveResult.Success

            when (remoteResult) {
                UserLocationSaveResult.Success -> {
                    householdSettingsRepository.saveWeatherLocation(weatherLocation)
                    householdSettingsRepository.saveDefaultDryingLocation(form.defaultDryingLocation)
                    _uiState.value = _uiState.value.copy(
                        householdForm = SettingsHouseholdFormState.from(
                            location = householdSettingsRepository.getWeatherLocation(),
                            defaultDryingLocation = householdSettingsRepository.getDefaultDryingLocation(),
                        ),
                        householdMessage = SettingsHouseholdMessage.Saved,
                    )
                }

                is UserLocationSaveResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        householdMessage = SettingsHouseholdMessage.RemoteSyncFailed(
                            type = remoteResult.type,
                            fallbackMessage = remoteResult.message,
                        ),
                    )
                }
            }
        }
    }

    override fun onCleared() {
        coroutineScope.cancel()
        super.onCleared()
    }

    companion object {
        fun createDefault(context: Context): SettingsViewModel {
            val api = BackendApiClient.create()
            val sessionTokenProvider = CompositeSessionTokenProvider(
                SecureAuthSessionStore(context),
                BuildConfigSessionTokenProvider(),
            )
            val repository = BackendWasherRepository(
                washerDataSource = RemoteWasherDataSource(api),
                accessTokenProvider = sessionTokenProvider::getAccessToken,
            )
            val userLocationRepository = BackendUserLocationRepository(
                dataSource = RemoteUserLocationDataSource(api),
                accessTokenProvider = sessionTokenProvider::getAccessToken,
            )
            return SettingsViewModel(
                washerRepository = repository,
                householdSettingsRepository = LocalHouseholdSettingsRepository(context),
                userLocationRepository = userLocationRepository,
                notificationPreferencesRepository = SharedPreferencesNotificationPreferencesRepository(context),
            )
        }
    }
}

data class SettingsScreenState(
    val isLoadingWashers: Boolean = false,
    val isCreatingWasher: Boolean = false,
    val isSavingWasherEdit: Boolean = false,
    val isRetiringWasherId: String? = null,
    val editingWasherId: String? = null,
    val washers: List<Washer> = emptyList(),
    val washerForm: SettingsWasherFormState = SettingsWasherFormState.defaultFor(),
    val householdForm: SettingsHouseholdFormState = SettingsHouseholdFormState.from(
        location = LocalHouseholdSettingsRepository.DefaultWeatherLocation,
        defaultDryingLocation = LocalHouseholdSettingsRepository.DefaultDryingLocation,
    ),
    val notificationPreferences: NotificationPreferences = NotificationPreferences(
        notificationsEnabled = true,
        idealHangingTimeEnabled = true,
        dryingCompleteEnabled = true,
        rainRiskEnabled = true,
    ),
    val washerMessage: SettingsWasherMessage? = null,
    val householdMessage: SettingsHouseholdMessage? = null,
)

data class SettingsWasherFormState(
    val name: String,
    val type: WasherType,
    val capacityKg: String,
    val energyLabel: WasherEnergyLabel?,
    val waterUsageLiters: String,
    val defaultSpinRpm: SpinSpeedRpm?,
    val isPrimary: Boolean,
) {
    val canSubmit: Boolean
        get() = isNameValid &&
            capacityKg.toWasherCapacityKg() != null &&
            waterUsageLiters.toWasherWaterUsageLiters() != null &&
            energyLabel != null &&
            defaultSpinRpm != null

    val isNameValid: Boolean
        get() = name.trim().isNotEmpty() && name.trim().length <= MaxWasherNameLength

    val isCapacityValid: Boolean
        get() = capacityKg.toWasherCapacityKg() != null

    val isWaterUsageValid: Boolean
        get() = waterUsageLiters.toWasherWaterUsageLiters() != null

    fun toCreateRemoteWasher(makePrimaryByDefault: Boolean): CreateRemoteWasher? {
        if (!canSubmit) return null
        return CreateRemoteWasher(
            name = name.trim(),
            type = type,
            capacityKg = capacityKg.toWasherCapacityKg(),
            energyLabel = energyLabel?.code,
            waterUsageLiters = waterUsageLiters.toWasherWaterUsageLiters(),
            defaultSpinRpm = defaultSpinRpm,
            isPrimary = isPrimary || makePrimaryByDefault,
        )
    }

    companion object {
        fun defaultFor(
            isPrimary: Boolean = true,
        ): SettingsWasherFormState {
            return SettingsWasherFormState(
                name = "",
                type = WasherType.FRONT_LOAD,
                capacityKg = "7",
                energyLabel = WasherEnergyLabel.A,
                waterUsageLiters = "45",
                defaultSpinRpm = SpinSpeedRpm.RPM_1200,
                isPrimary = isPrimary,
            )
        }

        fun from(washer: Washer): SettingsWasherFormState {
            return SettingsWasherFormState(
                name = washer.name,
                type = washer.type,
                capacityKg = washer.capacityKg?.toFormNumber() ?: "",
                energyLabel = washer.energyLabel?.let { code ->
                    WasherEnergyLabel.entries.firstOrNull { it.code == code }
                } ?: WasherEnergyLabel.A,
                waterUsageLiters = washer.waterUsageLiters?.toFormNumber() ?: "",
                defaultSpinRpm = washer.defaultSpinRpm ?: SpinSpeedRpm.RPM_1200,
                isPrimary = washer.isPrimary,
            )
        }
    }
}

const val MaxWasherNameLength = 80
const val MinWasherCapacityKg = 1.0
const val MaxWasherCapacityKg = 20.0
const val MinWasherWaterUsageLiters = 20.0
const val MaxWasherWaterUsageLiters = 200.0

private fun Double.toFormNumber(): String {
    return if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        toString()
    }
}

private fun String.toWasherCapacityKg(): Double? {
    return toOptionalPositiveDouble()?.takeIf { it in MinWasherCapacityKg..MaxWasherCapacityKg }
}

private fun String.toWasherWaterUsageLiters(): Double? {
    return toOptionalPositiveDouble()?.takeIf { it in MinWasherWaterUsageLiters..MaxWasherWaterUsageLiters }
}

private fun String.toOptionalPositiveDouble(): Double? {
    val normalized = trim().replace(",", ".")
    if (normalized.isEmpty()) return null
    val value = normalized.toDoubleOrNull() ?: return null
    return value.takeIf { it > 0.0 }
}

enum class SettingsNotificationPreference {
    ALL,
    IDEAL_HANGING_TIME,
    DRYING_COMPLETE,
    RAIN_RISK,
}

sealed interface SettingsWasherMessage {
    data class Created(val washerName: String) : SettingsWasherMessage

    data class Updated(val washerName: String) : SettingsWasherMessage

    data class Retired(val washerName: String) : SettingsWasherMessage

    data class Error(
        val type: RemoteDataErrorType,
        val fallbackMessage: String,
    ) : SettingsWasherMessage
}

data class SettingsHouseholdFormState(
    val locationLabel: String,
    val latitude: Double?,
    val longitude: Double?,
    val defaultDryingLocation: DryingLocation,
) {
    val canSubmit: Boolean
        get() = locationLabel.isNotBlank()

    val hasDeviceLocation: Boolean
        get() = latitude != null && longitude != null

    val visibleLocationTag: String
        get() = locationLabel.trim()

    fun toWeatherLocation(): WeatherLocation {
        return WeatherLocation(
            id = "home",
            label = visibleLocationTag,
            latitude = latitude,
            longitude = longitude,
        )
    }

    companion object {
        fun from(
            location: WeatherLocation,
            defaultDryingLocation: DryingLocation,
        ): SettingsHouseholdFormState {
            return SettingsHouseholdFormState(
                locationLabel = location.label,
                latitude = location.latitude,
                longitude = location.longitude,
                defaultDryingLocation = defaultDryingLocation,
            )
        }
    }
}

sealed interface SettingsHouseholdMessage {
    data object Saved : SettingsHouseholdMessage
    data object CurrentLocationSelected : SettingsHouseholdMessage
    data object CurrentLocationUnavailable : SettingsHouseholdMessage
    data object LocationPermissionDenied : SettingsHouseholdMessage

    data class RemoteSyncFailed(
        val type: RemoteDataErrorType,
        val fallbackMessage: String,
    ) : SettingsHouseholdMessage
}
