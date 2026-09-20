package com.tesis_pro.tenderapp.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tesis_pro.tenderapp.R
import com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType
import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.LoadSize
import com.tesis_pro.tenderapp.domain.model.ProgramCompletionOption
import com.tesis_pro.tenderapp.domain.model.SpinSpeedRpm
import com.tesis_pro.tenderapp.domain.model.Washer
import com.tesis_pro.tenderapp.domain.model.WasherType
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.ui.components.EnergyCostSummary
import com.tesis_pro.tenderapp.ui.components.GlyphKind
import com.tesis_pro.tenderapp.ui.components.TonalPill
import com.tesis_pro.tenderapp.ui.components.WeatherGlyph
import com.tesis_pro.tenderapp.ui.theme.TenderTheme
import java.text.DateFormat
import java.util.Date

data class NewLoadUiState(
    @param:StringRes val washerRes: Int,
    @param:StringRes val clothingTypeRes: Int,
    @param:StringRes val programRes: Int,
    @param:StringRes val spinSpeedRes: Int,
    @param:StringRes val dryingEstimateRes: Int,
    @param:StringRes val recommendationRes: Int,
)

private val NewLoadMockState = NewLoadUiState(
    washerRes = R.string.new_load_main_washer,
    clothingTypeRes = R.string.new_load_mixed_clothes,
    programRes = R.string.new_load_program_eco40,
    spinSpeedRes = R.string.new_load_high_spin,
    dryingEstimateRes = R.string.new_load_drying_estimate_value,
    recommendationRes = R.string.new_load_recommendation,
)

@Composable
fun NewLoadScreen(
    onLoadCreated: () -> Unit = {},
) {
    val context = LocalContext.current.applicationContext
    val viewModel = remember(context) { NewLoadViewModel.createDefault(context) }
    val screenState by viewModel.uiState.collectAsState()

    LaunchedEffect(screenState.result) {
        if (screenState.result is NewLoadResult.Success) {
            onLoadCreated()
        }
    }

    NewLoadScreenContent(
        state = NewLoadMockState,
        screenState = screenState,
        onCreateLoad = viewModel::createDefaultLoad,
        onSelectWasher = viewModel::selectWasher,
        onSelectClothingType = viewModel::selectClothingType,
        onSelectWashingProgram = viewModel::selectWashingProgram,
        onSelectDryingLocation = viewModel::selectDryingLocation,
        onSelectSpinRpm = viewModel::selectSpinRpm,
        onSelectLoadSize = viewModel::selectLoadSize,
        onSelectTargetReadyTime = viewModel::selectTargetReadyTime,
        onRetryCompletionPlan = viewModel::refreshCompletionPlan,
    )
}

@Composable
private fun NewLoadScreenContent(
    state: NewLoadUiState,
    screenState: NewLoadScreenState,
    onCreateLoad: () -> Unit,
    onSelectWasher: (String?) -> Unit,
    onSelectClothingType: (ClothingType) -> Unit,
    onSelectWashingProgram: (WashingProgram) -> Unit,
    onSelectDryingLocation: (DryingLocation) -> Unit,
    onSelectSpinRpm: (SpinSpeedRpm) -> Unit,
    onSelectLoadSize: (LoadSize) -> Unit,
    onSelectTargetReadyTime: (Int, Int) -> Unit,
    onRetryCompletionPlan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.new_load_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.new_load_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        EstimateCard(
            state = state,
            screenState = screenState,
        )
        CompletionPlanCard(
            screenState = screenState,
            onSelectTargetReadyTime = onSelectTargetReadyTime,
            onSelectWashingProgram = onSelectWashingProgram,
            onRetryCompletionPlan = onRetryCompletionPlan,
        )
        NewLoadFormCard(
            state = state,
            screenState = screenState,
            isSubmitting = screenState.isSubmitting,
            onCreateLoad = onCreateLoad,
            onSelectWasher = onSelectWasher,
            onSelectClothingType = onSelectClothingType,
            onSelectWashingProgram = onSelectWashingProgram,
            onSelectDryingLocation = onSelectDryingLocation,
            onSelectSpinRpm = onSelectSpinRpm,
            onSelectLoadSize = onSelectLoadSize,
        )
        screenState.result?.let { result ->
            NewLoadResultCard(result = result)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CompletionPlanCard(
    screenState: NewLoadScreenState,
    onSelectTargetReadyTime: (Int, Int) -> Unit,
    onSelectWashingProgram: (WashingProgram) -> Unit,
    onRetryCompletionPlan: () -> Unit,
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val plan = screenState.backendCompletionPlan
    val feasiblePrograms = plan?.recommendedPrograms.orEmpty()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.new_load_completion_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.new_load_completion_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = stringResource(R.string.new_load_completion_target_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = screenState.targetReadyAtEpochMillis.localizedTime(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = stringResource(R.string.new_load_completion_change_time),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            when {
                screenState.isLoadingCompletionPlan -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                        Text(
                            text = stringResource(R.string.new_load_completion_loading),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                screenState.completionPlanError != null -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.new_load_completion_error),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                        OutlinedButton(
                            onClick = onRetryCompletionPlan,
                            enabled = !screenState.isLoadingCompletionPlan,
                        ) {
                            Text(text = stringResource(R.string.new_load_completion_retry))
                        }
                    }
                }
                plan != null -> {
                    val summary = if (feasiblePrograms.isEmpty()) {
                        stringResource(R.string.new_load_completion_none_feasible)
                    } else {
                        stringResource(
                            R.string.new_load_completion_feasible_summary,
                            feasiblePrograms.localizedProgramList(),
                        )
                    }
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    plan.options.forEach { option ->
                        CompletionProgramRow(
                            option = option,
                            selected = option.program == screenState.selectedWashingProgram,
                            onClick = { onSelectWashingProgram(option.program) },
                        )
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        val targetDate = Date(screenState.targetReadyAtEpochMillis)
        @Suppress("DEPRECATION")
        val pickerState = rememberTimePickerState(
            initialHour = targetDate.hours,
            initialMinute = targetDate.minutes,
            is24Hour = android.text.format.DateFormat.is24HourFormat(LocalContext.current),
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = {
                Text(text = stringResource(R.string.new_load_completion_target_label))
            },
            text = {
                TimePicker(state = pickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                        onSelectTargetReadyTime(pickerState.hour, pickerState.minute)
                    },
                ) {
                    Text(text = stringResource(R.string.new_load_completion_confirm_time))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(text = stringResource(R.string.new_load_completion_cancel_time))
                }
            },
        )
    }
}

@Composable
private fun CompletionProgramRow(
    option: ProgramCompletionOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val waitingMinutes = (
        (option.dryingStartsAtEpochMillis - option.washingEndsAtEpochMillis) / 60_000L
    ).toInt().coerceAtLeast(0)
    val statusColors = TenderTheme.statusColors
    val containerColor = when {
        selected -> MaterialTheme.colorScheme.primaryContainer
        option.feasible -> statusColors.goodContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        selected -> MaterialTheme.colorScheme.onPrimaryContainer
        option.feasible -> statusColors.onGoodContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        contentColor = contentColor,
        border = if (selected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = option.program.localizedLabel(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = option.estimatedReadyAtEpochMillis.localizedTime(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = if (waitingMinutes > 0) {
                    stringResource(
                        R.string.new_load_completion_wash_wait_dry_format,
                        option.washingMinutes.localizedDuration(),
                        waitingMinutes.localizedDuration(),
                        option.estimatedDryingMinutes.localizedDuration(),
                    )
                } else {
                    stringResource(
                        R.string.new_load_completion_wash_dry_format,
                        option.washingMinutes.localizedDuration(),
                        option.estimatedDryingMinutes.localizedDuration(),
                    )
                },
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(
                    R.string.new_load_completion_total_format,
                    option.totalElapsedMinutes.localizedDuration(),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.78f),
            )
            Text(
                text = if (option.feasible) {
                    stringResource(
                        R.string.new_load_completion_margin_format,
                        option.marginMinutes.coerceAtLeast(0).localizedDuration(),
                    )
                } else {
                    stringResource(
                        R.string.new_load_completion_late_format,
                        (-option.marginMinutes).coerceAtLeast(0).localizedDuration(),
                    )
                },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            if (option.usesForecastExtrapolation) {
                Text(
                    text = stringResource(R.string.new_load_completion_extrapolated),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.78f),
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun EstimateCard(
    state: NewLoadUiState,
    screenState: NewLoadScreenState,

    ) {
    val preview = screenState.decisionPreview
    val statusColors = TenderTheme.statusColors
    val (container, content) = when (preview.profile) {
        NewLoadDecisionProfile.RECOMMENDED -> statusColors.goodContainer to statusColors.onGoodContainer
        NewLoadDecisionProfile.MONITOR -> statusColors.warnContainer to statusColors.onWarnContainer
        NewLoadDecisionProfile.WAIT_OR_INDOOR -> statusColors.badContainer to statusColors.onBadContainer
        NewLoadDecisionProfile.INDOOR_STABLE -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = container,
            contentColor = content,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.new_load_decision_preview_title),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(preview.profile.titleRes()),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                WeatherGlyph(
                    kind = preview.profile.glyph(),
                    tint = content,
                    modifier = Modifier.size(68.dp),
                )
            }
            Text(
                text = stringResource(
                    preview.profile.messageRes(),
                    preview.estimatedDryingMinutes.toHoursMinutes(),
                ),
                style = MaterialTheme.typography.bodyLarge
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DecisionSignalTile(
                    label = stringResource(R.string.new_load_signal_score),
                    value = stringResource(R.string.new_load_selection_score_compact_format, preview.selectionScore),
                    contentColor = content,
                )
                DecisionSignalTile(
                    label = stringResource(R.string.new_load_signal_dry_time),
                    value = preview.estimatedDryingMinutes.toHoursMinutes(),
                    contentColor = content,
                )
                DecisionSignalTile(
                    label = stringResource(R.string.new_load_signal_source),
                    value = preview.source.localizedSourceValue(preview.temperatureCelsius),
                    contentColor = content,
                )
            }
            Text(
                text = stringResource(R.string.new_load_decision_preview_note),
                style = MaterialTheme.typography.bodySmall,
                color = content.copy(alpha = 0.82f),
            )
            preview.estimatedCost?.let { cost ->
                EnergyCostSummary(cost = cost)
            }
            if (screenState.isLoadingPredictionPreview && preview.source == NewLoadDecisionSource.SELECTION_FALLBACK) {
                Text(
                    text = stringResource(R.string.new_load_decision_loading_backend),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                text = stringResource(R.string.new_load_quick_summary),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TonalPill(
                    text = screenState.selectedDryingLocation.localizedLabel(),
                    container = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
                TonalPill(
                    text = screenState.selectedClothingType.localizedLabel(),
                    container = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
                TonalPill(
                    text = screenState.selectedWashingProgram.localizedLabel(),
                    container = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
                TonalPill(
                    text = screenState.resolvedSpinRpm.localizedLabel(),
                    container = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
                TonalPill(
                    text = screenState.selectedLoadSize.localizedLabel(),
                    container = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun DecisionSignalTile(
    label: String,
    value: String,
    contentColor: Color,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = contentColor,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.78f),
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun NewLoadFormCard(
    state: NewLoadUiState,
    screenState: NewLoadScreenState,
    isSubmitting: Boolean,
    onCreateLoad: () -> Unit,
    onSelectWasher: (String?) -> Unit,
    onSelectClothingType: (ClothingType) -> Unit,
    onSelectWashingProgram: (WashingProgram) -> Unit,
    onSelectDryingLocation: (DryingLocation) -> Unit,
    onSelectSpinRpm: (SpinSpeedRpm) -> Unit,
    onSelectLoadSize: (LoadSize) -> Unit,
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
                text = stringResource(R.string.new_load_details),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OptionGroup(
                label = stringResource(R.string.new_load_washer),
                values = buildList {
                    add(null to stringResource(R.string.dashboard_no_washer))
                    addAll(screenState.washers.map { it.id to it.displayLabel() })
                },
                selectedValue = screenState.selectedWasherId,
                onSelect = onSelectWasher,
            )
            OptionGroup(
                label = stringResource(R.string.new_load_clothes),
                values = ClothingType.entries.map { it to it.localizedLabel() },
                selectedValue = screenState.selectedClothingType,
                onSelect = onSelectClothingType,
            )
            OptionGroup(
                label = stringResource(R.string.new_load_drying_location),
                values = DryingLocation.entries.map { it to it.localizedLabel() },
                selectedValue = screenState.selectedDryingLocation,
                onSelect = onSelectDryingLocation,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OptionGroup(
                    label = stringResource(R.string.new_load_program),
                    values = WashingProgram.entries.map { it to it.localizedLabel() },
                    selectedValue = screenState.selectedWashingProgram,
                    onSelect = onSelectWashingProgram,
                    modifier = Modifier.weight(1f),
                )
                OptionGroup(
                    label = stringResource(R.string.new_load_spin),
                    values = SpinSpeedRpm.entries.map { it to it.localizedLabel() },
                    selectedValue = screenState.resolvedSpinRpm,
                    onSelect = onSelectSpinRpm,
                    modifier = Modifier.weight(1f),
                )
            }
            OptionGroup(
                label = stringResource(R.string.new_load_load_size),
                values = LoadSize.entries.map { it to it.localizedLabel() },
                selectedValue = screenState.selectedLoadSize,
                onSelect = onSelectLoadSize,
            )
            Button(
                onClick = onCreateLoad,
                enabled = !isSubmitting && !screenState.isLoadingWashers,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(text = stringResource(R.string.new_load_create_action))
                }
            }
        }
    }
}

@Composable
private fun NewLoadResultCard(result: NewLoadResult) {
    val statusColors = TenderTheme.statusColors
    val isSuccess = result is NewLoadResult.Success
    val container = if (isSuccess) statusColors.goodContainer else statusColors.badContainer
    val content = if (isSuccess) statusColors.onGoodContainer else statusColors.onBadContainer
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = container,
        contentColor = content,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (isSuccess) {
                    stringResource(R.string.new_load_create_success_title)
                } else {
                    stringResource(R.string.new_load_create_error_title)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when (result) {
                    is NewLoadResult.Success -> stringResource(result.successMessageRes())
                    is NewLoadResult.Error -> result.localizedMessage()
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun <T> OptionGroup(
    label: String,
    values: List<Pair<T, String>>,
    selectedValue: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = values.firstOrNull { it.first == selectedValue }?.second
        ?: values.firstOrNull()?.second.orEmpty()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = selectedText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text = stringResource(R.string.new_load_dropdown_indicator),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(),
            ) {
                values.forEach { (value, text) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = text,
                                fontWeight = if (value == selectedValue) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                            )
                        },
                        onClick = {
                            expanded = false
                            onSelect(value)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
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
}

@Composable
private fun Washer.displayLabel(): String {
    val capacity = capacityKg?.let { " - ${it.toInt()} kg" }.orEmpty()
    return "${name} (${type.localizedLabel()})$capacity"
}

@Composable
private fun SpinSpeedRpm.localizedLabel(): String {
    return stringResource(R.string.spin_speed_rpm_format, rpm)
}

@Composable
private fun LoadSize.localizedLabel(): String {
    return when (this) {
        LoadSize.SMALL -> stringResource(R.string.load_size_small)
        LoadSize.MEDIUM -> stringResource(R.string.load_size_medium)
        LoadSize.LARGE -> stringResource(R.string.load_size_large)
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
private fun ClothingType.localizedLabel(): String {
    return when (this) {
        ClothingType.LIGHT_CLOTHES -> stringResource(R.string.dashboard_clothing_light)
        ClothingType.HEAVY_CLOTHES -> stringResource(R.string.dashboard_clothing_heavy)
        ClothingType.BEDDING -> stringResource(R.string.dashboard_clothing_bedding)
        ClothingType.DELICATES -> stringResource(R.string.dashboard_clothing_delicates)
        ClothingType.MIXED -> stringResource(R.string.dashboard_clothing_mixed)
    }
}

@Composable
private fun WashingProgram.localizedLabel(): String {
    return when (this) {
        WashingProgram.QUICK -> stringResource(R.string.dashboard_program_quick)
        WashingProgram.NORMAL -> stringResource(R.string.dashboard_program_normal)
        WashingProgram.ECO -> stringResource(R.string.dashboard_program_eco)
        WashingProgram.DELICATE -> stringResource(R.string.dashboard_program_delicate)
    }
}

@Composable
private fun List<WashingProgram>.localizedProgramList(): String {
    val labels = mutableListOf<String>()
    for (program in this) {
        labels += program.localizedLabel()
    }
    return labels.joinToString()
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

private fun NewLoadDecisionProfile.titleRes(): Int {
    return when (this) {
        NewLoadDecisionProfile.RECOMMENDED -> R.string.new_load_decision_recommended_title
        NewLoadDecisionProfile.MONITOR -> R.string.new_load_decision_monitor_title
        NewLoadDecisionProfile.WAIT_OR_INDOOR -> R.string.new_load_decision_wait_indoor_title
        NewLoadDecisionProfile.INDOOR_STABLE -> R.string.new_load_decision_indoor_title
    }
}

private fun NewLoadDecisionProfile.messageRes(): Int {
    return when (this) {
        NewLoadDecisionProfile.RECOMMENDED -> R.string.new_load_decision_recommended_message
        NewLoadDecisionProfile.MONITOR -> R.string.new_load_decision_monitor_message
        NewLoadDecisionProfile.WAIT_OR_INDOOR -> R.string.new_load_decision_wait_indoor_message
        NewLoadDecisionProfile.INDOOR_STABLE -> R.string.new_load_decision_indoor_message
    }
}

private fun NewLoadDecisionProfile.glyph(): GlyphKind {
    return when (this) {
        NewLoadDecisionProfile.RECOMMENDED -> GlyphKind.SUN
        NewLoadDecisionProfile.MONITOR -> GlyphKind.CLOUD
        NewLoadDecisionProfile.WAIT_OR_INDOOR -> GlyphKind.RAIN
        NewLoadDecisionProfile.INDOOR_STABLE -> GlyphKind.CLOUD
    }
}

@Composable
private fun NewLoadDecisionSource.localizedSourceValue(temperatureCelsius: Int?): String {
    val source = when (this) {
        NewLoadDecisionSource.BACKEND_PREDICTION -> stringResource(R.string.new_load_decision_source_backend_prediction)
        NewLoadDecisionSource.OPEN_METEO -> stringResource(R.string.new_load_decision_source_open_meteo)
        NewLoadDecisionSource.MET_NO -> stringResource(R.string.new_load_decision_source_met_no)
        NewLoadDecisionSource.BACKEND_MOCK -> stringResource(R.string.new_load_decision_source_mock)
        NewLoadDecisionSource.LOCAL_FALLBACK -> stringResource(R.string.new_load_decision_source_local_fallback)
        NewLoadDecisionSource.SELECTION_FALLBACK -> stringResource(R.string.new_load_decision_source_selection_fallback)
        NewLoadDecisionSource.UNKNOWN -> stringResource(R.string.new_load_decision_source_unknown)
    }
    return if (temperatureCelsius == null) {
        source
    } else {
        stringResource(R.string.new_load_decision_source_value_format, source, temperatureCelsius)
    }
}

private fun Int.toHoursMinutes(): String {
    val hours = this / 60
    val minutes = this % 60
    return if (hours > 0) {
        "${hours} h ${minutes} min"
    } else {
        "${minutes} min"
    }
}

@Composable
private fun Int.localizedDuration(): String {
    val duration = coerceAtLeast(0)
    val hours = duration / 60
    val minutes = duration % 60
    return if (hours > 0) {
        stringResource(R.string.new_load_completion_duration_hours_minutes, hours, minutes)
    } else {
        stringResource(R.string.new_load_completion_duration_minutes, minutes)
    }
}

private fun Long.localizedTime(): String {
    return DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(this))
}

@Composable
private fun NewLoadResult.Error.localizedMessage(): String {
    return when (type) {
        RemoteDataErrorType.AUTHENTICATION -> stringResource(R.string.new_load_error_authentication)
        RemoteDataErrorType.NETWORK -> stringResource(R.string.new_load_error_network)
        RemoteDataErrorType.SERVER -> stringResource(R.string.new_load_error_server)
        RemoteDataErrorType.INVALID_RESPONSE -> stringResource(R.string.new_load_error_invalid_response)
        RemoteDataErrorType.NOT_FOUND -> stringResource(R.string.new_load_error_not_found)
        RemoteDataErrorType.UNKNOWN -> fallbackMessage
    }
}

@StringRes
private fun NewLoadResult.Success.successMessageRes(): Int {
    return when {
        idealHangingReminderScheduled && pickupReminderScheduled ->
            R.string.new_load_create_success_message_with_all_reminders
        idealHangingReminderScheduled -> R.string.new_load_create_success_message_with_reminder
        pickupReminderScheduled -> R.string.new_load_create_success_message_with_pickup_reminder
        else -> R.string.new_load_create_success_message
    }
}
