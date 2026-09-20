package com.tesis_pro.tenderapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tesis_pro.tenderapp.R
import com.tesis_pro.tenderapp.data.location.DeviceLocationResult
import com.tesis_pro.tenderapp.data.notification.NotificationRuntimePermissionState
import com.tesis_pro.tenderapp.data.settings.AppAppearancePreferences
import com.tesis_pro.tenderapp.data.settings.AppLanguagePreference
import com.tesis_pro.tenderapp.data.settings.AppThemePreference
import com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType
import com.tesis_pro.tenderapp.data.repository.LocalHouseholdSettingsRepository
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.SpinSpeedRpm
import com.tesis_pro.tenderapp.domain.model.Washer
import com.tesis_pro.tenderapp.domain.model.WasherEnergyLabel
import com.tesis_pro.tenderapp.domain.model.WasherType
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.notification.NotificationPreferences
import com.tesis_pro.tenderapp.ui.components.TonalPill
import com.tesis_pro.tenderapp.ui.theme.TenderTheme

data class SettingsPreferenceUi(
    val preference: SettingsNotificationPreference,
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int,
    val enabled: Boolean,
    val switchEnabled: Boolean = true,
)

@Composable
fun SettingsScreen(
    notificationPermissionState: NotificationRuntimePermissionState =
        NotificationRuntimePermissionState.NOT_REQUIRED,
    onRequestNotificationPermission: () -> Unit = {},
    appearancePreferences: AppAppearancePreferences = AppAppearancePreferences(),
    onLanguagePreferenceChanged: (AppLanguagePreference) -> Unit = {},
    onThemePreferenceChanged: (AppThemePreference) -> Unit = {},
    onUseCurrentLocation: ((DeviceLocationResult) -> Unit) -> Unit = { callback ->
        callback(DeviceLocationResult.Unavailable)
    },
    onOpenSession: () -> Unit = {},
) {
    val context = LocalContext.current
    val applicationContext = context.applicationContext
    val viewModel = remember(applicationContext) { SettingsViewModel.createDefault(applicationContext) }
    val screenState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.settings_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SettingsSection(title = stringResource(R.string.settings_account)) {
            StaticValueRow(
                label = stringResource(R.string.settings_session),
                value = stringResource(R.string.settings_required),
            )
            StaticValueRow(
                label = stringResource(R.string.settings_email_verification),
                value = stringResource(R.string.settings_email_verification_value),
            )
            OutlinedButton(
                onClick = onOpenSession,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.settings_open_session))
            }
        }
        SettingsSection(title = stringResource(R.string.settings_language_theme)) {
            PreferenceSelector(
                label = stringResource(R.string.settings_language),
                options = AppLanguagePreference.entries,
                selected = appearancePreferences.language,
                onSelected = onLanguagePreferenceChanged,
                optionLabel = { it.localizedLabel() },
            )
            PreferenceSelector(
                label = stringResource(R.string.settings_theme),
                options = AppThemePreference.entries,
                selected = appearancePreferences.theme,
                onSelected = onThemePreferenceChanged,
                optionLabel = { it.localizedLabel() },
            )
            Text(
                text = stringResource(R.string.settings_language_theme_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        SettingsSection(title = stringResource(R.string.settings_notifications)) {
            NotificationDeliveryPanel(
                runtimePermissionAllowsNotifications = notificationPermissionState.allowsNotifications,
                notificationsEnabled = screenState.notificationPreferences.notificationsEnabled,
            )
            QuietHoursPanel(
                preferences = screenState.notificationPreferences,
                onEnabledChanged = viewModel::setQuietHoursEnabled,
                onStartChanged = viewModel::setQuietHoursStart,
                onEndChanged = viewModel::setQuietHoursEnd,
            )
            NotificationRuntimePermissionRow(
                state = notificationPermissionState,
                onRequestPermission = onRequestNotificationPermission,
            )
            notificationPreferenceItems(
                state = screenState,
                runtimePermissionAllowsNotifications = notificationPermissionState.allowsNotifications,
            ).forEach { preference ->
                PreferenceRow(
                    preference = preference,
                    onPreferenceChanged = { notificationPreference, enabled ->
                        if (
                            notificationPreference == SettingsNotificationPreference.ALL &&
                            enabled &&
                            notificationPermissionState == NotificationRuntimePermissionState.SHOULD_REQUEST
                        ) {
                            viewModel.toggleNotificationPreference(notificationPreference, true)
                            onRequestNotificationPermission()
                        } else {
                            viewModel.toggleNotificationPreference(notificationPreference, enabled)
                        }
                    },
                )
            }
        }
        SettingsSection(title = stringResource(R.string.settings_washers)) {
            WasherCreationForm(
                form = screenState.washerForm,
                isCreating = screenState.isCreatingWasher,
                isEditing = screenState.editingWasherId != null,
                isSavingEdit = screenState.isSavingWasherEdit,
                onNameChanged = viewModel::onWasherNameChanged,
                onTypeChanged = viewModel::onWasherTypeChanged,
                onCapacityChanged = viewModel::onWasherCapacityChanged,
                onEnergyLabelChanged = viewModel::onWasherEnergyLabelChanged,
                onWaterUsageChanged = viewModel::onWasherWaterUsageChanged,
                onDefaultSpinRpmChanged = viewModel::onWasherDefaultSpinRpmChanged,
                onPrimaryChanged = viewModel::onWasherPrimaryChanged,
                onCreate = viewModel::createWasher,
                onSaveEdit = viewModel::saveWasherEdit,
                onCancelEdit = viewModel::cancelWasherEdit,
                location = screenState.householdForm.locationLabel,
            )
            if (screenState.isLoadingWashers) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(R.string.settings_washers_loading),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            if (screenState.washers.isEmpty() && !screenState.isLoadingWashers) {
                Text(
                    text = stringResource(R.string.settings_washers_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            screenState.washers.forEach { washer ->
                RemoteWasherRow(
                    washer = washer,
                    isRetiring = screenState.isRetiringWasherId == washer.id,
                    isEditing = screenState.editingWasherId == washer.id,
                    onEdit = { viewModel.startEditingWasher(washer) },
                    onRetire = { viewModel.retireWasher(washer) },
                )
            }
            screenState.washerMessage?.let { message ->
                Text(
                    text = message.localizedMessage(),
                    style = MaterialTheme.typography.bodySmall,
                    color = when (message) {
                        is SettingsWasherMessage.Created -> TenderTheme.statusColors.onGoodContainer
                        is SettingsWasherMessage.Updated -> TenderTheme.statusColors.onGoodContainer
                        is SettingsWasherMessage.Retired -> TenderTheme.statusColors.onGoodContainer
                        is SettingsWasherMessage.Error -> TenderTheme.statusColors.onBadContainer
                    },
                )
            }
        }
        SettingsSection(title = stringResource(R.string.settings_household_defaults)) {
            StaticValueRow(
                label = stringResource(R.string.settings_primary_washer),
                value = stringResource(R.string.settings_main_washer),
            )
            StaticValueRow(
                label = stringResource(R.string.settings_default_program),
                value = stringResource(R.string.settings_default_program_value),
            )
            HouseholdDefaultsForm(
                form = screenState.householdForm,
                message = screenState.householdMessage,
                onLocationLabelChanged = viewModel::onHouseholdLocationLabelChanged,
                onDefaultDryingLocationChanged = viewModel::onDefaultDryingLocationChanged,
                onUseCurrentLocation = {
                    onUseCurrentLocation { result ->
                        when (result) {
                            is DeviceLocationResult.Success -> viewModel.onCurrentLocationSelected(
                                latitude = result.location.latitude,
                                longitude = result.location.longitude,
                                label = context.getString(R.string.settings_current_location_label),
                            )

                            DeviceLocationResult.PermissionDenied ->
                                viewModel.onCurrentLocationPermissionDenied()

                            DeviceLocationResult.Unavailable ->
                                viewModel.onCurrentLocationUnavailable()
                        }
                    }
                },
                onOpenMap = { openHouseholdLocationInMap(context, screenState.householdForm) },
                onSave = viewModel::saveHouseholdDefaults,
            )
            StaticValueRow(
                label = stringResource(R.string.settings_reminder_lead_time),
                value = stringResource(R.string.settings_reminder_lead_time_value),
            )
        }
    }
}

@Composable
private fun HouseholdDefaultsForm(
    form: SettingsHouseholdFormState,
    message: SettingsHouseholdMessage?,
    onLocationLabelChanged: (String) -> Unit,
    onDefaultDryingLocationChanged: (DryingLocation) -> Unit,
    onUseCurrentLocation: () -> Unit,
    onOpenMap: () -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = form.locationLabel,
            onValueChange = onLocationLabelChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.settings_household_location_name)) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
        )
        CompactSettingSelector(
            label = stringResource(R.string.settings_default_drying_method),
            options = DryingLocation.entries,
            selected = form.defaultDryingLocation,
            onSelected = onDefaultDryingLocationChanged,
            optionLabel = { it.localizedLabel() },
        )
        Text(
            text = stringResource(R.string.settings_location_weather_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.settings_household_visible_tag_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        StaticValueRow(
            label = stringResource(R.string.settings_household_visible_tag),
            value = form.visibleLocationTag.ifBlank {
                stringResource(R.string.settings_required)
            },
        )
        TonalPill(
            text = if (form.hasDeviceLocation) {
                stringResource(R.string.settings_household_weather_point_linked)
            } else {
                stringResource(R.string.settings_household_weather_point_default)
            },
            container = if (form.hasDeviceLocation) {
                TenderTheme.statusColors.goodContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = if (form.hasDeviceLocation) {
                TenderTheme.statusColors.onGoodContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            showDot = true,
        )
        OutlinedButton(
            onClick = onUseCurrentLocation,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.settings_use_current_location))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onOpenMap,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.settings_open_map))
            }
            Button(
                onClick = onSave,
                enabled = form.canSubmit,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.settings_save_household_defaults))
            }
        }
        if (message != null) {
            Text(
                text = message.localizedMessage(),
                style = MaterialTheme.typography.bodySmall,
                color = message.messageColor(),
            )
        }
    }
}

@Composable
private fun <T> PreferenceSelector(
    label: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    optionLabel: @Composable (T) -> String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { option ->
                if (option == selected) {
                    Button(
                        onClick = { onSelected(option) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(optionLabel(option))
                    }
                } else {
                    OutlinedButton(
                        onClick = { onSelected(option) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(optionLabel(option))
                    }
                }
            }
        }
    }
}

@Composable
private fun AppLanguagePreference.localizedLabel(): String {
    return when (this) {
        AppLanguagePreference.SYSTEM -> stringResource(R.string.settings_system_language)
        AppLanguagePreference.SPANISH -> stringResource(R.string.settings_language_spanish)
        AppLanguagePreference.ENGLISH -> stringResource(R.string.settings_language_english)
    }
}

@Composable
private fun AppThemePreference.localizedLabel(): String {
    return when (this) {
        AppThemePreference.SYSTEM -> stringResource(R.string.settings_system_theme)
        AppThemePreference.LIGHT -> stringResource(R.string.settings_theme_light)
        AppThemePreference.DARK -> stringResource(R.string.settings_theme_dark)
    }
}

@Composable
private fun WasherCreationForm(
    form: SettingsWasherFormState,
    isCreating: Boolean,
    isEditing: Boolean,
    isSavingEdit: Boolean,
    onNameChanged: (String) -> Unit,
    onTypeChanged: (WasherType) -> Unit,
    onCapacityChanged: (String) -> Unit,
    onEnergyLabelChanged: (WasherEnergyLabel?) -> Unit,
    onWaterUsageChanged: (String) -> Unit,
    onDefaultSpinRpmChanged: (SpinSpeedRpm?) -> Unit,
    onPrimaryChanged: (Boolean) -> Unit,
    onCreate: () -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    location: String,
) {
    val isBusy = isCreating || isSavingEdit
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(
                if (isEditing) {
                    R.string.settings_washer_form_edit_title
                } else {
                    R.string.settings_washer_form_title
                },
            ),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        OutlinedTextField(
            value = form.name,
            onValueChange = onNameChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.settings_washer_name)) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            isError = form.name.isNotBlank() && !form.isNameValid,
            supportingText = {
                if (form.name.isNotBlank() && !form.isNameValid) {
                    Text(stringResource(R.string.settings_washer_name_error))
                }
            },
        )
        WasherTypeSelector(
            selected = form.type,
            onSelected = onTypeChanged,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = form.capacityKg,
                onValueChange = onCapacityChanged,
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.settings_washer_capacity_kg)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = form.capacityKg.isNotBlank() && !form.isCapacityValid,
                supportingText = {
                    if (form.capacityKg.isNotBlank() && !form.isCapacityValid) {
                        Text(stringResource(R.string.settings_washer_capacity_error))
                    }
                },
            )
            OutlinedTextField(
                value = form.waterUsageLiters,
                onValueChange = onWaterUsageChanged,
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.settings_washer_water_usage_liters)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = form.waterUsageLiters.isNotBlank() && !form.isWaterUsageValid,
                supportingText = {
                    if (form.waterUsageLiters.isNotBlank() && !form.isWaterUsageValid) {
                        Text(stringResource(R.string.settings_washer_water_usage_error))
                    }
                },
            )
        }
        WasherEnergyLabelSelector(
            selected = form.energyLabel,
            onSelected = onEnergyLabelChanged,
        )
        WasherDefaultSpinSelector(
            selected = form.defaultSpinRpm,
            onSelected = onDefaultSpinRpmChanged,
        )
        StaticValueRow(
            label = stringResource(R.string.settings_washer_location),
            value = location,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_make_primary_washer),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(R.string.settings_make_primary_washer_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = form.isPrimary,
                onCheckedChange = onPrimaryChanged,
            )
        }
        Button(
            onClick = if (isEditing) onSaveEdit else onCreate,
            enabled = form.canSubmit && !isBusy,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isBusy) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(18.dp),
                    strokeWidth = 2.dp,
                )
            }
            Text(
                stringResource(
                    if (isEditing) {
                        R.string.settings_save_washer_changes
                    } else {
                        R.string.settings_add_washer
                    },
                )
            )
        }
        if (isEditing) {
            OutlinedButton(
                onClick = onCancelEdit,
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.settings_cancel_washer_edit))
            }
        }
    }
}

@Composable
private fun WasherEnergyLabelSelector(
    selected: WasherEnergyLabel?,
    onSelected: (WasherEnergyLabel?) -> Unit,
) {
    val options: List<WasherEnergyLabel?> = WasherEnergyLabel.entries
    CompactSettingSelector(
        label = stringResource(R.string.settings_washer_energy_label),
        options = options,
        selected = selected ?: WasherEnergyLabel.A,
        onSelected = onSelected,
        optionLabel = { it?.code ?: WasherEnergyLabel.A.code },
    )
}

@Composable
private fun WasherDefaultSpinSelector(
    selected: SpinSpeedRpm?,
    onSelected: (SpinSpeedRpm?) -> Unit,
) {
    val options: List<SpinSpeedRpm?> = SpinSpeedRpm.entries
    CompactSettingSelector(
        label = stringResource(R.string.settings_washer_default_spin),
        options = options,
        selected = selected ?: SpinSpeedRpm.RPM_1200,
        onSelected = onSelected,
        optionLabel = { (it ?: SpinSpeedRpm.RPM_1200).localizedLabel() },
    )
}

@Composable
private fun WasherTypeSelector(
    selected: WasherType,
    onSelected: (WasherType) -> Unit,
) {
    CompactSettingSelector(
        label = stringResource(R.string.settings_washer_type),
        options = WasherType.entries,
        selected = selected,
        onSelected = onSelected,
        optionLabel = { it.localizedLabel() },
    )
}

@Composable
private fun <T> CompactSettingSelector(
    label: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    optionLabel: @Composable (T) -> String,
) {
    var expanded by remember { mutableStateOf(false) }
    val labeledOptions = options.map { option -> option to optionLabel(option) }
    val selectedLabel = labeledOptions.firstOrNull { it.first == selected }?.second
        ?: labeledOptions.firstOrNull()?.second.orEmpty()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    text = selectedLabel,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.settings_change),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(),
            ) {
                labeledOptions.forEach { (option, optionText) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = optionText,
                                fontWeight = if (option == selected) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                            )
                        },
                        onClick = {
                            expanded = false
                            onSelected(option)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun RemoteWasherRow(
    washer: Washer,
    isRetiring: Boolean,
    isEditing: Boolean,
    onEdit: () -> Unit,
    onRetire: () -> Unit,
) {
    val statusColors = TenderTheme.statusColors
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = washer.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            if (washer.isPrimary) {
                TonalPill(
                    text = stringResource(R.string.settings_primary),
                    container = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TonalPill(
                text = stringResource(R.string.settings_ready),
                container = statusColors.goodContainer,
                contentColor = statusColors.onGoodContainer,
                showDot = true,
            )
            Text(
                text = washer.type.localizedLabel(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        StaticValueRow(
            label = stringResource(R.string.settings_washer_location),
            value = stringResource(R.string.settings_location_value),
        )
        StaticValueRow(
            label = stringResource(R.string.settings_washer_capacity),
            value = washer.capacityKg?.let {
                stringResource(R.string.settings_capacity_kg_format, it)
            } ?: "-",
        )
        washer.energyLabel?.takeIf { it.isNotBlank() }?.let { energy ->
            StaticValueRow(
                label = stringResource(R.string.settings_washer_energy_label),
                value = energy,
            )
        }
        washer.waterUsageLiters?.let { waterUsage ->
            StaticValueRow(
                label = stringResource(R.string.settings_washer_water_usage_liters),
                value = stringResource(R.string.settings_water_liters_format, waterUsage),
            )
        }
        washer.defaultSpinRpm?.let { spin ->
            StaticValueRow(
                label = stringResource(R.string.settings_washer_default_spin),
                value = spin.localizedLabel(),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onEdit,
                enabled = !isRetiring && !isEditing,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    stringResource(
                        if (isEditing) {
                            R.string.settings_editing_washer
                        } else {
                            R.string.settings_edit_washer
                        },
                    )
                )
            }
            OutlinedButton(
                onClick = onRetire,
                enabled = !isRetiring,
                modifier = Modifier.weight(1f),
            ) {
                if (isRetiring) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(18.dp),
                        strokeWidth = 2.dp,
                    )
                }
                Text(stringResource(R.string.settings_retire_washer))
            }
        }
        Text(
            text = stringResource(R.string.settings_retire_washer_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun WeatherLocation.localizedLabel(): String {
    return when (id) {
        LocalHouseholdSettingsRepository.DefaultWeatherLocation.id -> stringResource(R.string.settings_location_value)
        else -> label
    }
}

@Composable
private fun DryingLocation.localizedLabel(): String {
    return when (this) {
        DryingLocation.INDOOR -> stringResource(R.string.new_load_drying_location_indoor)
        DryingLocation.BALCONY -> stringResource(R.string.new_load_drying_location_balcony)
        DryingLocation.OUTDOOR_LINE -> stringResource(R.string.new_load_drying_location_outdoor_line)
        DryingLocation.PATIO -> stringResource(R.string.new_load_drying_location_patio)
        DryingLocation.LAUNDRY_ROOM -> stringResource(R.string.new_load_drying_location_laundry_room)
    }
}

private fun openHouseholdLocationInMap(
    context: android.content.Context,
    form: SettingsHouseholdFormState,
) {
    val latitude = form.latitude
    val longitude = form.longitude
    val uri = if (latitude != null && longitude != null) {
        Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(form.locationLabel)})")
    } else {
        Uri.parse("geo:0,0?q=${Uri.encode(form.locationLabel)}")
    }
    val intent = Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}

@Composable
private fun SettingsHouseholdMessage.localizedMessage(): String {
    return when (this) {
        SettingsHouseholdMessage.Saved -> stringResource(R.string.settings_household_saved)
        SettingsHouseholdMessage.CurrentLocationSelected ->
            stringResource(R.string.settings_current_location_selected)
        SettingsHouseholdMessage.CurrentLocationUnavailable ->
            stringResource(R.string.settings_current_location_unavailable)
        SettingsHouseholdMessage.LocationPermissionDenied ->
            stringResource(R.string.settings_location_permission_denied)
        is SettingsHouseholdMessage.RemoteSyncFailed ->
            stringResource(R.string.settings_household_remote_sync_failed)
    }
}

@Composable
private fun SettingsHouseholdMessage.messageColor(): androidx.compose.ui.graphics.Color {
    return when (this) {
        SettingsHouseholdMessage.Saved,
        SettingsHouseholdMessage.CurrentLocationSelected -> TenderTheme.statusColors.onGoodContainer
        SettingsHouseholdMessage.CurrentLocationUnavailable,
        SettingsHouseholdMessage.LocationPermissionDenied,
        is SettingsHouseholdMessage.RemoteSyncFailed -> MaterialTheme.colorScheme.onErrorContainer
    }
}

@Composable
private fun WasherType.localizedLabel(): String {
    return when (this) {
        WasherType.FRONT_LOAD -> stringResource(R.string.settings_front_load)
        WasherType.TOP_LOAD -> stringResource(R.string.settings_top_load)
        WasherType.WASHER_DRYER -> stringResource(R.string.settings_washer_dryer)
        WasherType.OTHER -> stringResource(R.string.settings_other_washer_type)
    }
}

@Composable
private fun SpinSpeedRpm.localizedLabel(): String {
    return stringResource(R.string.spin_speed_rpm_format, rpm)
}

@Composable
private fun SettingsWasherMessage.localizedMessage(): String {
    return when (this) {
        is SettingsWasherMessage.Created -> stringResource(R.string.settings_washer_created, washerName)
        is SettingsWasherMessage.Updated -> stringResource(R.string.settings_washer_updated, washerName)
        is SettingsWasherMessage.Retired -> stringResource(R.string.settings_washer_retired, washerName)
        is SettingsWasherMessage.Error -> when (type) {
            RemoteDataErrorType.AUTHENTICATION -> stringResource(R.string.settings_washer_error_authentication)
            RemoteDataErrorType.NETWORK -> stringResource(R.string.settings_washer_error_network)
            RemoteDataErrorType.SERVER -> stringResource(R.string.settings_washer_error_server)
            RemoteDataErrorType.INVALID_RESPONSE -> stringResource(R.string.settings_washer_error_invalid_response)
            RemoteDataErrorType.NOT_FOUND -> stringResource(R.string.settings_washer_error_not_found)
            RemoteDataErrorType.UNKNOWN -> fallbackMessage
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

private fun notificationPreferenceItems(
    state: SettingsScreenState,
    runtimePermissionAllowsNotifications: Boolean,
): List<SettingsPreferenceUi> {
    val preferences = state.notificationPreferences
    val globalEnabled = preferences.notificationsEnabled && runtimePermissionAllowsNotifications
    val categoriesEnabled = globalEnabled
    return listOf(
        SettingsPreferenceUi(
            preference = SettingsNotificationPreference.ALL,
            titleRes = R.string.settings_basic_alerts,
            descriptionRes = R.string.settings_basic_alerts_description,
            enabled = globalEnabled,
        ),
        SettingsPreferenceUi(
            preference = SettingsNotificationPreference.RAIN_RISK,
            titleRes = R.string.settings_rain_alerts,
            descriptionRes = R.string.settings_rain_alerts_description,
            enabled = preferences.rainRiskEnabled,
            switchEnabled = categoriesEnabled,
        ),
        SettingsPreferenceUi(
            preference = SettingsNotificationPreference.IDEAL_HANGING_TIME,
            titleRes = R.string.settings_best_drying_window,
            descriptionRes = R.string.settings_best_drying_window_description,
            enabled = preferences.idealHangingTimeEnabled,
            switchEnabled = categoriesEnabled,
        ),
        SettingsPreferenceUi(
            preference = SettingsNotificationPreference.DRYING_COMPLETE,
            titleRes = R.string.settings_pickup_reminder,
            descriptionRes = R.string.settings_pickup_reminder_description,
            enabled = preferences.dryingCompleteEnabled,
            switchEnabled = categoriesEnabled,
        ),
    )
}

@Composable
private fun NotificationDeliveryPanel(
    runtimePermissionAllowsNotifications: Boolean,
    notificationsEnabled: Boolean,
) {
    val statusColors = TenderTheme.statusColors
    val pushEnabled = runtimePermissionAllowsNotifications && notificationsEnabled
    val pushContainer = if (pushEnabled) {
        statusColors.goodContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val pushContent = if (pushEnabled) {
        statusColors.onGoodContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_notification_channels),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TonalPill(
                text = stringResource(R.string.settings_notification_channel_push),
                container = pushContainer,
                contentColor = pushContent,
                showDot = true,
            )
            TonalPill(
                text = stringResource(R.string.settings_notification_channel_in_app),
                container = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                showDot = true,
            )
        }
        Text(
            text = stringResource(R.string.settings_notification_channels_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun QuietHoursPanel(
    preferences: NotificationPreferences,
    onEnabledChanged: (Boolean) -> Unit,
    onStartChanged: (String) -> Unit,
    onEndChanged: (String) -> Unit,
) {
    val timeOptions = remember {
        listOf(
            "20:00",
            "21:00",
            "22:00",
            "23:00",
            "00:00",
            "06:00",
            "07:00",
            "08:00",
            "09:00",
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_quiet_hours),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(R.string.settings_quiet_hours_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = preferences.quietHoursEnabled,
                onCheckedChange = onEnabledChanged,
            )
        }
        if (preferences.quietHoursEnabled) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    CompactSettingSelector(
                        label = stringResource(R.string.settings_quiet_hours_start),
                        options = timeOptions,
                        selected = preferences.quietHoursStart,
                        onSelected = onStartChanged,
                        optionLabel = { it },
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    CompactSettingSelector(
                        label = stringResource(R.string.settings_quiet_hours_end),
                        options = timeOptions,
                        selected = preferences.quietHoursEnd,
                        onSelected = onEndChanged,
                        optionLabel = { it },
                    )
                }
            }
        } else {
            TonalPill(
                text = stringResource(R.string.settings_quiet_hours_disabled),
                container = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun NotificationRuntimePermissionRow(
    state: NotificationRuntimePermissionState,
    onRequestPermission: () -> Unit,
) {
    val statusColors = TenderTheme.statusColors
    val statusText = when (state) {
        NotificationRuntimePermissionState.NOT_REQUIRED -> R.string.settings_notification_permission_not_required
        NotificationRuntimePermissionState.GRANTED -> R.string.settings_notification_permission_granted
        NotificationRuntimePermissionState.SHOULD_REQUEST -> R.string.settings_notification_permission_required
    }
    val statusContainer = if (state.allowsNotifications) {
        statusColors.goodContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val statusContent = if (state.allowsNotifications) {
        statusColors.onGoodContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_notification_permission),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(R.string.settings_notification_permission_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TonalPill(
                text = stringResource(statusText),
                container = statusContainer,
                contentColor = statusContent,
            )
        }
        if (state == NotificationRuntimePermissionState.SHOULD_REQUEST) {
            OutlinedButton(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.settings_notification_permission_action))
            }
        }
    }
}

private val NotificationRuntimePermissionState.allowsNotifications: Boolean
    get() = this != NotificationRuntimePermissionState.SHOULD_REQUEST

@Composable
private fun PreferenceRow(
    preference: SettingsPreferenceUi,
    onPreferenceChanged: (SettingsNotificationPreference, Boolean) -> Unit,
) {
    val statusColors = TenderTheme.statusColors
    val effectivelyEnabled = preference.enabled && preference.switchEnabled
    val (statusContainer, statusContent) = if (effectivelyEnabled) {
        statusColors.goodContainer to statusColors.onGoodContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(preference.titleRes),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(preference.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TonalPill(
                text = stringResource(
                    if (effectivelyEnabled) {
                        R.string.settings_preference_on
                    } else {
                        R.string.settings_preference_paused
                    }
                ),
                container = statusContainer,
                contentColor = statusContent,
            )
        }
        Switch(
            checked = preference.enabled,
            enabled = preference.switchEnabled,
            onCheckedChange = { enabled ->
                onPreferenceChanged(preference.preference, enabled)
            }
        )
    }
}

@Composable
private fun StaticValueRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}
