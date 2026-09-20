package com.tesis_pro.tenderapp.ui.screens

import com.tesis_pro.tenderapp.data.remote.RemoteWasherDataSource
import com.tesis_pro.tenderapp.data.remote.RemoteUserLocationDataSource
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
import com.tesis_pro.tenderapp.data.repository.BackendUserLocationRepository
import com.tesis_pro.tenderapp.data.repository.BackendWasherRepository
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.SpinSpeedRpm
import com.tesis_pro.tenderapp.domain.model.WasherEnergyLabel
import com.tesis_pro.tenderapp.domain.model.WasherType
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.notification.NotificationPreferences
import com.tesis_pro.tenderapp.domain.notification.NotificationPreferencesRepository
import com.tesis_pro.tenderapp.domain.repository.HouseholdSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsViewModelTest {
    @Test
    fun toggleNotificationPreference_disablesGlobalNotificationOptIn() {
        val viewModel = settingsViewModel()

        viewModel.toggleNotificationPreference(SettingsNotificationPreference.ALL, false)

        val preferences = viewModel.uiState.value.notificationPreferences
        assertFalse(preferences.notificationsEnabled)
        assertTrue(preferences.rainRiskEnabled)
        assertTrue(preferences.idealHangingTimeEnabled)
        assertTrue(preferences.dryingCompleteEnabled)
    }

    @Test
    fun toggleNotificationPreference_disablesSpecificNotificationCategory() {
        val viewModel = settingsViewModel()

        viewModel.toggleNotificationPreference(SettingsNotificationPreference.RAIN_RISK, false)

        val preferences = viewModel.uiState.value.notificationPreferences
        assertTrue(preferences.notificationsEnabled)
        assertFalse(preferences.rainRiskEnabled)
        assertTrue(preferences.idealHangingTimeEnabled)
        assertTrue(preferences.dryingCompleteEnabled)
    }

    @Test
    fun quietHours_updatesConfigurableNotificationWindow() {
        val viewModel = settingsViewModel()

        viewModel.setQuietHoursEnabled(true)
        viewModel.setQuietHoursStart("21:00")
        viewModel.setQuietHoursEnd("08:00")

        val preferences = viewModel.uiState.value.notificationPreferences
        assertTrue(preferences.quietHoursEnabled)
        assertEquals("21:00", preferences.quietHoursStart)
        assertEquals("08:00", preferences.quietHoursEnd)
    }

    @Test
    fun createWasher_sendsCustomFormValuesToBackend() {
        val api = FakeSettingsBackendApi()
        val viewModel = settingsViewModel(
            api = api,
            accessToken = "token-1",
        )

        viewModel.onWasherNameChanged("Balcony washer")
        viewModel.onWasherTypeChanged(WasherType.WASHER_DRYER)
        viewModel.onWasherCapacityChanged("8,5")
        viewModel.onWasherEnergyLabelChanged(WasherEnergyLabel.A_PLUS_PLUS)
        viewModel.onWasherWaterUsageChanged("39")
        viewModel.onWasherDefaultSpinRpmChanged(SpinSpeedRpm.RPM_1400)
        viewModel.onWasherPrimaryChanged(false)

        viewModel.createWasher()

        val request = api.lastWasherRequest
        assertEquals("Balcony washer", request?.name)
        assertEquals("WASHER_DRYER", request?.type)
        assertEquals(8.5, request?.capacityKg ?: 0.0, 0.001)
        assertEquals("A++", request?.energyLabel)
        assertEquals(39.0, request?.waterUsageLiters ?: 0.0, 0.001)
        assertEquals(1400, request?.defaultSpinRpm)
        assertEquals(true, request?.isPrimary)
    }

    @Test
    fun createWasher_doesNotSubmitWhenNameIsBlank() {
        val api = FakeSettingsBackendApi()
        val viewModel = settingsViewModel(
            api = api,
            accessToken = "token-1",
        )

        viewModel.onWasherNameChanged(" ")

        viewModel.createWasher()

        assertEquals(null, api.lastWasherRequest)
    }

    @Test
    fun createWasher_doesNotSubmitWhenWasherMetadataIsOutsideRealisticBounds() {
        val api = FakeSettingsBackendApi()
        val viewModel = settingsViewModel(
            api = api,
            accessToken = "token-1",
        )

        viewModel.onWasherNameChanged("Oversized washer")
        viewModel.onWasherCapacityChanged("35")
        viewModel.onWasherWaterUsageChanged("10")

        viewModel.createWasher()

        assertEquals(null, api.lastWasherRequest)
        assertEquals(false, viewModel.uiState.value.washerForm.canSubmit)
        assertEquals(false, viewModel.uiState.value.washerForm.isCapacityValid)
        assertEquals(false, viewModel.uiState.value.washerForm.isWaterUsageValid)
    }

    @Test
    fun createWasher_doesNotSubmitWhenNameExceedsBackendLimit() {
        val api = FakeSettingsBackendApi()
        val viewModel = settingsViewModel(
            api = api,
            accessToken = "token-1",
        )

        viewModel.onWasherNameChanged("A".repeat(MaxWasherNameLength + 1))

        viewModel.createWasher()

        assertEquals(null, api.lastWasherRequest)
        assertEquals(false, viewModel.uiState.value.washerForm.isNameValid)
    }

    @Test
    fun editWasher_prefillsFormAndSendsUpdateToBackend() {
        val api = FakeSettingsBackendApi(
            washers = listOf(
                WasherResponseDto(
                    id = "washer-1",
                    name = "Main washer",
                    type = "FRONT_LOAD",
                    capacityKg = 7.0,
                    energyLabel = "A",
                    waterUsageLiters = 45.0,
                    defaultSpinRpm = 1200,
                    isPrimary = true,
                ),
            ),
        )
        val viewModel = settingsViewModel(
            api = api,
            accessToken = "token-1",
        )

        viewModel.startEditingWasher(viewModel.uiState.value.washers.first())

        assertEquals("washer-1", viewModel.uiState.value.editingWasherId)
        assertEquals("Main washer", viewModel.uiState.value.washerForm.name)
        assertEquals("7", viewModel.uiState.value.washerForm.capacityKg)
        assertEquals(WasherEnergyLabel.A, viewModel.uiState.value.washerForm.energyLabel)
        assertEquals("45", viewModel.uiState.value.washerForm.waterUsageLiters)
        assertEquals(SpinSpeedRpm.RPM_1200, viewModel.uiState.value.washerForm.defaultSpinRpm)

        viewModel.onWasherNameChanged("Updated washer")
        viewModel.onWasherTypeChanged(WasherType.WASHER_DRYER)
        viewModel.onWasherCapacityChanged("8")
        viewModel.onWasherEnergyLabelChanged(WasherEnergyLabel.A_PLUS)
        viewModel.onWasherWaterUsageChanged("41")
        viewModel.onWasherDefaultSpinRpmChanged(SpinSpeedRpm.RPM_1400)
        viewModel.onWasherPrimaryChanged(true)
        viewModel.saveWasherEdit()

        val request = api.lastUpdateWasherRequest
        assertEquals("Bearer token-1", api.lastUpdateWasherAuthorization)
        assertEquals("washer-1", api.lastUpdatedWasherId)
        assertEquals("Updated washer", request?.name)
        assertEquals("WASHER_DRYER", request?.type)
        assertEquals(8.0, request?.capacityKg ?: 0.0, 0.001)
        assertEquals("A+", request?.energyLabel)
        assertEquals(41.0, request?.waterUsageLiters ?: 0.0, 0.001)
        assertEquals(1400, request?.defaultSpinRpm)
        assertEquals(true, request?.isPrimary)
        assertEquals(null, viewModel.uiState.value.editingWasherId)
        assertEquals("Updated washer", viewModel.uiState.value.washers.first().name)
        assertTrue(viewModel.uiState.value.washerMessage is SettingsWasherMessage.Updated)
    }

    @Test
    fun editWasher_usesDefaultSpinWhenLegacyWasherHasNoSpinValue() {
        val api = FakeSettingsBackendApi(
            washers = listOf(
                WasherResponseDto(
                    id = "washer-legacy",
                    name = "Legacy washer",
                    type = "FRONT_LOAD",
                    capacityKg = 7.0,
                    energyLabel = "A",
                    waterUsageLiters = 45.0,
                    defaultSpinRpm = null,
                    isPrimary = true,
                ),
            ),
        )
        val viewModel = settingsViewModel(
            api = api,
            accessToken = "token-1",
        )

        viewModel.startEditingWasher(viewModel.uiState.value.washers.first())

        assertEquals(SpinSpeedRpm.RPM_1200, viewModel.uiState.value.washerForm.defaultSpinRpm)
    }

    @Test
    fun editWasher_usesDefaultEnergyWhenLegacyWasherHasNoEnergyLabel() {
        val api = FakeSettingsBackendApi(
            washers = listOf(
                WasherResponseDto(
                    id = "washer-legacy",
                    name = "Legacy washer",
                    type = "FRONT_LOAD",
                    capacityKg = 7.0,
                    energyLabel = null,
                    waterUsageLiters = 45.0,
                    defaultSpinRpm = 1200,
                    isPrimary = true,
                ),
            ),
        )
        val viewModel = settingsViewModel(
            api = api,
            accessToken = "token-1",
        )

        viewModel.startEditingWasher(viewModel.uiState.value.washers.first())

        assertEquals(WasherEnergyLabel.A, viewModel.uiState.value.washerForm.energyLabel)
    }

    @Test
    fun cancelWasherEdit_returnsFormToCreateMode() {
        val api = FakeSettingsBackendApi(
            washers = listOf(
                WasherResponseDto(
                    id = "washer-1",
                    name = "Main washer",
                    type = "FRONT_LOAD",
                    capacityKg = 7.0,
                    energyLabel = "A",
                    waterUsageLiters = 45.0,
                    defaultSpinRpm = 1200,
                    isPrimary = true,
                ),
            ),
        )
        val viewModel = settingsViewModel(
            api = api,
            accessToken = "token-1",
        )

        viewModel.startEditingWasher(viewModel.uiState.value.washers.first())
        viewModel.cancelWasherEdit()

        assertEquals(null, viewModel.uiState.value.editingWasherId)
        assertEquals("", viewModel.uiState.value.washerForm.name)
        assertEquals(false, viewModel.uiState.value.washerForm.isPrimary)
    }

    @Test
    fun retireWasher_removesWasherFromSettingsList() {
        val api = FakeSettingsBackendApi(
            washers = listOf(
                WasherResponseDto(
                    id = "washer-1",
                    name = "Main washer",
                    type = "FRONT_LOAD",
                    capacityKg = 7.0,
                    energyLabel = "A",
                    waterUsageLiters = 45.0,
                    defaultSpinRpm = 1200,
                    isPrimary = true,
                ),
            ),
        )
        val viewModel = settingsViewModel(
            api = api,
            accessToken = "token-1",
        )

        viewModel.retireWasher(viewModel.uiState.value.washers.first())

        assertEquals("Bearer token-1", api.lastRetireWasherAuthorization)
        assertEquals("washer-1", api.lastRetiredWasherId)
        assertTrue(viewModel.uiState.value.washers.isEmpty())
        assertTrue(viewModel.uiState.value.washerMessage is SettingsWasherMessage.Retired)
    }

    @Test
    fun saveHouseholdDefaults_persistsLocationAndDefaultDryingLocation() {
        val api = FakeSettingsBackendApi()
        val householdRepository = FakeSettingsHouseholdSettingsRepository()
        val viewModel = settingsViewModel(
            api = api,
            accessToken = "token-1",
            householdSettingsRepository = householdRepository,
        )

        viewModel.onHouseholdLocationLabelChanged("Balcony home")
        viewModel.onCurrentLocationSelected(
            latitude = -34.6037,
            longitude = -58.3816,
            label = "Current home",
        )
        viewModel.onDefaultDryingLocationChanged(DryingLocation.BALCONY)

        viewModel.saveHouseholdDefaults()

        assertEquals("Balcony home", householdRepository.location.label)
        assertEquals(-34.6037, householdRepository.location.latitude ?: 0.0, 0.0001)
        assertEquals(-58.3816, householdRepository.location.longitude ?: 0.0, 0.0001)
        assertEquals(DryingLocation.BALCONY, householdRepository.savedDefaultDryingLocation)
        assertEquals(SettingsHouseholdMessage.Saved, viewModel.uiState.value.householdMessage)
        assertEquals("Bearer token-1", api.lastUserLocationAuthorization)
        assertEquals("home", api.lastUserLocationRequest?.locationId)
        assertEquals("Balcony home", api.lastUserLocationRequest?.label)
        assertEquals(-34.6037, api.lastUserLocationRequest?.latitude ?: 0.0, 0.0001)
        assertEquals(-58.3816, api.lastUserLocationRequest?.longitude ?: 0.0, 0.0001)
    }

    @Test
    fun saveHouseholdDefaults_doesNotPersistLocalLocationWhenRemoteSyncFails() {
        val householdRepository = FakeSettingsHouseholdSettingsRepository()
        val viewModel = settingsViewModel(
            accessToken = null,
            householdSettingsRepository = householdRepository,
        )

        viewModel.onHouseholdLocationLabelChanged("Current home")
        viewModel.onCurrentLocationSelected(
            latitude = -34.6037,
            longitude = -58.3816,
            label = "Current home",
        )

        viewModel.saveHouseholdDefaults()

        assertEquals("Home patio", householdRepository.location.label)
        assertTrue(viewModel.uiState.value.householdMessage is SettingsHouseholdMessage.RemoteSyncFailed)
    }

    @Test
    fun onCurrentLocationUnavailable_surfacesHouseholdMessage() {
        val viewModel = settingsViewModel()

        viewModel.onCurrentLocationUnavailable()

        assertEquals(
            SettingsHouseholdMessage.CurrentLocationUnavailable,
            viewModel.uiState.value.householdMessage,
        )
    }

    @Test
    fun onCurrentLocationSelected_keepsVisibleHouseholdTagAndHidesCoordinatesInFormState() {
        val viewModel = settingsViewModel()

        viewModel.onHouseholdLocationLabelChanged("Casa")
        viewModel.onCurrentLocationSelected(
            latitude = -34.6037,
            longitude = -58.3816,
            label = "Current home",
        )

        val form = viewModel.uiState.value.householdForm
        assertEquals("Casa", form.visibleLocationTag)
        assertTrue(form.hasDeviceLocation)
        assertEquals(SettingsHouseholdMessage.CurrentLocationSelected, viewModel.uiState.value.householdMessage)
    }
}

private fun settingsViewModel(
    api: FakeSettingsBackendApi = FakeSettingsBackendApi(),
    accessToken: String? = null,
    householdSettingsRepository: HouseholdSettingsRepository = FakeSettingsHouseholdSettingsRepository(),
): SettingsViewModel {
    return SettingsViewModel(
        washerRepository = BackendWasherRepository(
            washerDataSource = RemoteWasherDataSource(api),
            accessTokenProvider = { accessToken },
        ),
        userLocationRepository = BackendUserLocationRepository(
            dataSource = RemoteUserLocationDataSource(api),
            accessTokenProvider = { accessToken },
        ),
        householdSettingsRepository = householdSettingsRepository,
        notificationPreferencesRepository = FakeNotificationPreferencesRepository(),
        coroutineScope = CoroutineScope(Dispatchers.Unconfined),
    )
}

private class FakeSettingsHouseholdSettingsRepository : HouseholdSettingsRepository {
    var location = WeatherLocation(
        id = "home",
        label = "Home patio",
        latitude = null,
        longitude = null,
    )
    var savedDefaultDryingLocation = DryingLocation.PATIO

    override fun getWeatherLocation(): WeatherLocation = location

    override fun getDefaultDryingLocation(): DryingLocation = savedDefaultDryingLocation

    override fun saveWeatherLocation(location: WeatherLocation) {
        this.location = location
    }

    override fun saveDefaultDryingLocation(location: DryingLocation) {
        savedDefaultDryingLocation = location
    }
}

private class FakeNotificationPreferencesRepository : NotificationPreferencesRepository {
    private var preferences = NotificationPreferences(
        notificationsEnabled = true,
        idealHangingTimeEnabled = true,
        dryingCompleteEnabled = true,
        rainRiskEnabled = true,
    )

    override fun currentPreferences(): NotificationPreferences = preferences

    override fun updatePreferences(preferences: NotificationPreferences) {
        this.preferences = preferences
    }
}

private class FakeSettingsBackendApi(
    private val washers: List<WasherResponseDto> = emptyList(),
) : TenderBackendApi {
    var lastWasherRequest: SaveWasherRequestDto? = null
    var lastUpdateWasherAuthorization: String? = null
    var lastUpdateWasherRequest: SaveWasherRequestDto? = null
    var lastUpdatedWasherId: String? = null
    var lastRetireWasherAuthorization: String? = null
    var lastRetiredWasherId: String? = null
    var lastUserLocationAuthorization: String? = null
    var lastUserLocationRequest: SaveUserLocationRequestDto? = null

    override suspend fun health(): HealthResponseDto {
        error("Not used by settings view model tests")
    }

    override suspend fun supabaseHealth(): SupabaseHealthResponseDto {
        error("Not used by settings view model tests")
    }

    override suspend fun currentWeather(
        authorization: String,
        locationId: String,
    ): WeatherSnapshotResponseDto {
        error("Not used by settings view model tests")
    }

    override suspend fun listLaundryLoads(authorization: String): List<LaundryLoadResponseDto> {
        error("Not used by settings view model tests")
    }

    override suspend fun listWashers(authorization: String): List<WasherResponseDto> {
        return washers
    }

    override suspend fun createWasher(
        authorization: String,
        request: SaveWasherRequestDto,
    ): WasherResponseDto {
        lastWasherRequest = request
        return WasherResponseDto(
            id = "washer-1",
            name = request.name,
            type = request.type,
            capacityKg = request.capacityKg,
            energyLabel = request.energyLabel,
            waterUsageLiters = request.waterUsageLiters,
            defaultSpinRpm = request.defaultSpinRpm,
            isPrimary = request.isPrimary,
        )
    }

    override suspend fun updateWasher(
        authorization: String,
        washerId: String,
        request: SaveWasherRequestDto,
    ): WasherResponseDto {
        lastUpdateWasherAuthorization = authorization
        lastUpdatedWasherId = washerId
        lastUpdateWasherRequest = request
        return WasherResponseDto(
            id = washerId,
            name = request.name,
            type = request.type,
            capacityKg = request.capacityKg,
            energyLabel = request.energyLabel,
            waterUsageLiters = request.waterUsageLiters,
            defaultSpinRpm = request.defaultSpinRpm,
            isPrimary = request.isPrimary,
        )
    }

    override suspend fun retireWasher(
        authorization: String,
        washerId: String,
    ) {
        lastRetireWasherAuthorization = authorization
        lastRetiredWasherId = washerId
    }

    override suspend fun createLaundryLoad(
        authorization: String,
        request: CreateLaundryLoadRequestDto,
    ): LaundryLoadResponseDto {
        error("Not used by settings view model tests")
    }

    override suspend fun updateLaundryLoadStatus(
        authorization: String,
        loadId: String,
        request: UpdateLaundryLoadStatusRequestDto,
    ): LaundryLoadResponseDto {
        error("Not used by settings view model tests")
    }

    override suspend fun saveUserLocation(
        authorization: String,
        request: SaveUserLocationRequestDto,
    ): UserLocationResponseDto {
        lastUserLocationAuthorization = authorization
        lastUserLocationRequest = request
        return UserLocationResponseDto(
            id = request.locationId,
            label = request.label,
            latitude = request.latitude,
            longitude = request.longitude,
            isPrimary = true,
            updatedAt = "2026-07-12T12:00:00.000Z",
        )
    }
}
