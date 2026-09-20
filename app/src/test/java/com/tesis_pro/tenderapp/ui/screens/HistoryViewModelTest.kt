package com.tesis_pro.tenderapp.ui.screens

import com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType
import com.tesis_pro.tenderapp.data.repository.LaundryHistoryRepository
import com.tesis_pro.tenderapp.data.repository.LaundryMutationResult
import com.tesis_pro.tenderapp.data.repository.LaundryRefreshResult
import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.repository.HouseholdSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryViewModelTest {
    @Test
    fun refresh_mapsHomeLocationIdToVisibleHouseholdLabel() {
        val repository = FakeLaundryHistoryRepository(
            initialLoads = listOf(sampleLoad(locationLabel = "home")),
            mutationResult = LaundryMutationResult.Success(sampleLoad()),
        )
        val viewModel = HistoryViewModel(
            laundryRepository = repository,
            householdSettingsRepository = FakeHistoryHouseholdSettingsRepository(
                WeatherLocation(
                    id = "home",
                    label = "Patio de casa",
                    latitude = null,
                    longitude = null,
                ),
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value as HistoryScreenState.Content

        assertEquals("Patio de casa", state.loads.first().location)
    }

    @Test
    fun discardLoad_keepsCurrentHistoryVisibleWhenBackendMutationFails() {
        val repository = FakeLaundryHistoryRepository(
            initialLoads = listOf(sampleLoad()),
            mutationResult = LaundryMutationResult.Error(
                type = RemoteDataErrorType.NETWORK,
                message = "Unable to reach TenderApp backend.",
            ),
        )
        val viewModel = HistoryViewModel(
            laundryRepository = repository,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        viewModel.discardLoad("load-1")

        val state = viewModel.uiState.value
        assertTrue(state is HistoryScreenState.Content)

        val content = state as HistoryScreenState.Content
        assertEquals(1, content.loads.size)
        assertEquals("load-1", content.loads.first().id)
        assertFalse(content.isUpdating)
        assertEquals(RemoteDataErrorType.NETWORK, content.mutationError?.type)
    }
}

private class FakeHistoryHouseholdSettingsRepository(
    private val location: WeatherLocation,
) : HouseholdSettingsRepository {
    override fun getWeatherLocation(): WeatherLocation = location

    override fun getDefaultDryingLocation(): DryingLocation = DryingLocation.PATIO

    override fun saveWeatherLocation(location: WeatherLocation) = Unit

    override fun saveDefaultDryingLocation(location: DryingLocation) = Unit
}

private class FakeLaundryHistoryRepository(
    initialLoads: List<LaundryLoad>,
    private val mutationResult: LaundryMutationResult,
) : LaundryHistoryRepository {
    private var loads: List<LaundryLoad> = initialLoads

    override suspend fun refreshHistory(): LaundryRefreshResult {
        return LaundryRefreshResult.Success
    }

    override fun currentHistory(): List<LaundryLoad> = loads

    override suspend fun updateLaundryLoadStatus(
        loadId: String,
        status: LaundryLoadStatus,
    ): LaundryMutationResult {
        return mutationResult
    }
}

private fun sampleLoad(
    status: LaundryLoadStatus = LaundryLoadStatus.DRYING,
    locationLabel: String = "Casa",
): LaundryLoad {
    return LaundryLoad(
        id = "load-1",
        washerId = "washer-1",
        clothingType = ClothingType.MIXED,
        washingProgram = WashingProgram.NORMAL,
        status = status,
        location = WeatherLocation(
            id = "home",
            label = locationLabel,
            latitude = null,
            longitude = null,
        ),
        dryingLocation = DryingLocation.PATIO,
        createdAtEpochMillis = 1_783_000_000_000L,
        startedAtEpochMillis = null,
        completedAtEpochMillis = null,
        prediction = null,
    )
}
