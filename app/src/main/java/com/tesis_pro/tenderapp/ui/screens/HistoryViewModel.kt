package com.tesis_pro.tenderapp.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import com.tesis_pro.tenderapp.data.auth.BuildConfigSessionTokenProvider
import com.tesis_pro.tenderapp.data.auth.CompositeSessionTokenProvider
import com.tesis_pro.tenderapp.data.auth.SecureAuthSessionStore
import com.tesis_pro.tenderapp.data.remote.BackendApiClient
import com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType
import com.tesis_pro.tenderapp.data.repository.BackendLaundryRepository
import com.tesis_pro.tenderapp.data.repository.LaundryHistoryRepository
import com.tesis_pro.tenderapp.data.repository.LaundryMutationResult
import com.tesis_pro.tenderapp.data.repository.LaundryRefreshResult
import com.tesis_pro.tenderapp.data.repository.LocalHouseholdSettingsRepository
import com.tesis_pro.tenderapp.data.remote.RemoteLaundryDataSource
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.repository.HouseholdSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val laundryRepository: LaundryHistoryRepository,
    householdSettingsRepository: HouseholdSettingsRepository = LocalHouseholdSettingsRepository(),
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
) : ViewModel() {
    private val householdLocation = householdSettingsRepository.getWeatherLocation()

    private val _uiState = MutableStateFlow<HistoryScreenState>(HistoryScreenState.Loading)
    val uiState: StateFlow<HistoryScreenState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = HistoryScreenState.Loading
        coroutineScope.launch {
            _uiState.value = when (val result = laundryRepository.refreshHistory()) {
                LaundryRefreshResult.Success -> {
                    val loads = laundryRepository.currentHistory()
                    if (loads.isEmpty()) {
                        HistoryScreenState.Empty
                    } else {
                        HistoryScreenState.Content(
                            loads = loads.map { it.toHistoryLoadUi(householdLocation) },
                        )
                    }
                }

                is LaundryRefreshResult.Error -> HistoryScreenState.Error(
                    type = result.type,
                    fallbackMessage = result.message,
                )
            }
        }
    }

    fun advanceLoadStatus(
        loadId: String,
        currentStatus: LaundryLoadStatus,
    ) {
        val nextStatus = currentStatus.nextStatus() ?: return
        updateLoadStatus(loadId, nextStatus)
    }

    fun discardLoad(loadId: String) {
        updateLoadStatus(loadId, LaundryLoadStatus.CANCELLED)
    }

    private fun updateLoadStatus(
        loadId: String,
        status: LaundryLoadStatus,
    ) {
        val previousState = _uiState.value
        if (previousState is HistoryScreenState.Content) {
            _uiState.value = previousState.copy(isUpdating = true, mutationError = null)
        } else {
            _uiState.value = HistoryScreenState.Loading
        }
        coroutineScope.launch {
            when (val result = laundryRepository.updateLaundryLoadStatus(loadId, status)) {
                is LaundryMutationResult.Success -> refresh()
                is LaundryMutationResult.Error -> {
                    _uiState.value = if (previousState is HistoryScreenState.Content) {
                        previousState.copy(
                            isUpdating = false,
                            mutationError = HistoryMutationError(
                                type = result.type,
                                fallbackMessage = result.message,
                            ),
                        )
                    } else {
                        HistoryScreenState.Error(
                            type = result.type,
                            fallbackMessage = result.message,
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        coroutineScope.cancel()
        super.onCleared()
    }

    companion object {
        fun createDefault(context: Context): HistoryViewModel {
            val api = BackendApiClient.create()
            val sessionTokenProvider = CompositeSessionTokenProvider(
                SecureAuthSessionStore(context),
                BuildConfigSessionTokenProvider(),
            )
            val repository = BackendLaundryRepository(
                laundryDataSource = RemoteLaundryDataSource(api),
                accessTokenProvider = sessionTokenProvider::getAccessToken,
            )
            return HistoryViewModel(
                laundryRepository = repository,
                householdSettingsRepository = LocalHouseholdSettingsRepository(context),
            )
        }
    }
}

sealed interface HistoryScreenState {
    data object Loading : HistoryScreenState

    data object Empty : HistoryScreenState

    data class Error(
        val type: RemoteDataErrorType,
        val fallbackMessage: String,
    ) : HistoryScreenState

    data class Content(
        val loads: List<HistoryLoadUi>,
        val isUpdating: Boolean = false,
        val mutationError: HistoryMutationError? = null,
    ) : HistoryScreenState
}

data class HistoryMutationError(
    val type: RemoteDataErrorType,
    val fallbackMessage: String,
)

private fun LaundryLoad.toHistoryLoadUi(householdLocation: WeatherLocation): HistoryLoadUi {
    return HistoryLoadUi(
        id = id,
        title = clothingType.name,
        dateEpochMillis = createdAtEpochMillis,
        status = status,
        program = washingProgram.name,
        location = location.toVisibleHistoryLabel(householdLocation),
        isActive = status.isActive(),
    )
}

private fun WeatherLocation.toVisibleHistoryLabel(householdLocation: WeatherLocation): String {
    val rawLabel = label.trim()
    val rawId = id.trim()
    val householdLabel = householdLocation.label.trim().ifBlank { "Hogar" }
    val matchesHousehold = rawId.equals(householdLocation.id, ignoreCase = true) ||
        rawLabel.equals(householdLocation.id, ignoreCase = true)

    return when {
        matchesHousehold -> householdLabel
        rawLabel.equals("home", ignoreCase = true) -> householdLabel
        rawLabel.isNotBlank() -> rawLabel
        rawId.isNotBlank() -> rawId
        else -> householdLabel
    }
}

private fun LaundryLoadStatus.isActive(): Boolean {
    return this != LaundryLoadStatus.COMPLETED && this != LaundryLoadStatus.CANCELLED
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
