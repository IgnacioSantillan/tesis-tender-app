package com.tesis_pro.tenderapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tesis_pro.tenderapp.R
import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.DryingVerdict
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCost
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostConfidence
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostLevel
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.ui.components.EnergyCostSummary
import com.tesis_pro.tenderapp.ui.components.GlyphKind
import com.tesis_pro.tenderapp.ui.components.MetricKind
import com.tesis_pro.tenderapp.ui.components.MetricTile
import com.tesis_pro.tenderapp.ui.components.StatBar
import com.tesis_pro.tenderapp.ui.components.TonalPill
import com.tesis_pro.tenderapp.ui.components.WeatherGlyph
import com.tesis_pro.tenderapp.ui.theme.TenderAppTheme
import com.tesis_pro.tenderapp.ui.theme.TenderTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.ceil
import kotlinx.coroutines.delay

data class DashboardUiState(
    val activeLoadId: String,
    val verdict: WeatherVerdictUi,
    val recommendation: ActionRecommendationUi,
    val rainRiskAlert: RainRiskAlertUi?,
    val weather: WeatherMetricsUi,
    val loadEstimate: LoadEstimateUi,
    val currentStatus: CurrentStatusUi,
    val hangWindow: DryingHangWindowUi?,
    val suitabilityScore: Int,
    val sourceLabel: String,
)

data class DashboardEmptyRecommendationUiState(
    val verdict: WeatherVerdictUi,
    val rainRiskAlert: RainRiskAlertUi?,
    val weather: WeatherMetricsUi,
    val dryingLocation: DryingLocation,
    val estimatedDryingMinutes: Int,
    val suitabilityScore: Int,
    val sourceLabel: String,
)

data class WeatherVerdictUi(
    val verdict: DryingVerdict,
    val reason: String,
)

data class ActionRecommendationUi(
    val action: DashboardRecommendationAction,
    val estimatedDryingMinutes: Int,
)

enum class DashboardRecommendationAction {
    START_NOW,
    START_WITH_CAUTION,
    WAIT_OR_USE_INDOOR,
    PREPARE_TO_HANG,
    MONITOR_DRYING,
    CHECK_DRYNESS,
}

data class RainRiskAlertUi(
    val severity: RainRiskSeverity,
    val rainProbabilityPercent: Int,
)

enum class RainRiskSeverity {
    WATCH,
    HIGH,
}

data class WeatherMetricsUi(
    val temperature: String,
    val humidity: String,
    val wind: String,
)

data class LoadEstimateUi(
    val washer: String?,
    val hasWasherLinked: Boolean = false,
    val clothingType: ClothingType,
    val program: WashingProgram,
    val dryingLocation: DryingLocation,
    val estimatedDryingMinutes: Int,
    val estimatedCost: EstimatedWashingCost?,
)

data class CurrentStatusUi(
    val status: LaundryLoadStatus,
    val estimatedDryingMinutes: Int,
    val progress: Float,
    val sourceLabel: String,
    val dryingStartedAtEpochMillis: Long? = null,
    val dryingEstimatedPickupAtEpochMillis: Long? = null,
)

data class DryingHangWindowUi(
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val hourlySlots: List<DryingHourlyForecastSlotUi>,
)

data class DryingHourlyForecastSlotUi(
    val forecastForEpochMillis: Long,
    val verdict: DryingVerdict,
    val suitabilityScore: Int,
    val temperature: String,
    val rainProbabilityPercent: Int,
)

sealed interface DashboardScreenState {
    data object Loading : DashboardScreenState

    data class Empty(
        val recommendation: DashboardEmptyRecommendationUiState? = null,
    ) : DashboardScreenState

    data class Error(
        val message: String,
    ) : DashboardScreenState

    data class Content(
        val dashboard: DashboardUiState,
        val isUpdatingStatus: Boolean = false,
        val statusUpdateError: String? = null,
    ) : DashboardScreenState
}

@Composable
fun DashboardScreen(
    onCreateNewLoad: () -> Unit = {},
) {
    val context = LocalContext.current.applicationContext
    val viewModel = remember(context) { DashboardViewModel.createDefault(context) }
    val state by viewModel.uiState.collectAsState()

    DashboardScreenContent(
        state = state,
        onAdvanceActiveLoadStatus = viewModel::advanceActiveLoadStatus,
        onDiscardActiveLoad = viewModel::discardActiveLoad,
        onCreateNewLoad = onCreateNewLoad,
    )
}

@Composable
fun DashboardScreenContent(
    state: DashboardScreenState,
    onAdvanceActiveLoadStatus: () -> Unit = {},
    onDiscardActiveLoad: () -> Unit = {},
    onCreateNewLoad: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        when (state) {
            is DashboardScreenState.Empty -> DashboardEmptyContent(
                state = state,
                onCreateNewLoad = onCreateNewLoad,
            )

            is DashboardScreenState.Error -> StateMessageCard(
                title = stringResource(R.string.dashboard_error_title),
                message = state.message
            )

            DashboardScreenState.Loading -> LoadingStateCard()
            is DashboardScreenState.Content -> DashboardSuccessContent(
                state = state.dashboard,
                isUpdatingStatus = state.isUpdatingStatus,
                statusUpdateError = state.statusUpdateError,
                onAdvanceActiveLoadStatus = onAdvanceActiveLoadStatus,
                onDiscardActiveLoad = onDiscardActiveLoad,
            )
        }
    }
}

@Composable
private fun DashboardEmptyContent(
    state: DashboardScreenState.Empty,
    onCreateNewLoad: () -> Unit,
) {
    StateMessageCard(
        title = stringResource(R.string.dashboard_empty_title),
        message = stringResource(R.string.dashboard_empty_message),
    )

    state.recommendation?.let { recommendation ->
        WeatherVerdictCard(
            verdict = recommendation.verdict,
            weather = recommendation.weather,
            suitabilityScore = recommendation.suitabilityScore,
        )
        recommendation.rainRiskAlert?.let { alert ->
            RainRiskAlertCard(alert = alert)
        }
        GeneralRecommendationCard(
            recommendation = recommendation,
            onCreateNewLoad = onCreateNewLoad,
        )
        Text(
            text = stringResource(R.string.dashboard_source, weatherSourceLabelText(recommendation.sourceLabel)),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DashboardSuccessContent(
    state: DashboardUiState,
    isUpdatingStatus: Boolean,
    statusUpdateError: String?,
    onAdvanceActiveLoadStatus: () -> Unit,
    onDiscardActiveLoad: () -> Unit,
) {
    WeatherVerdictCard(
        verdict = state.verdict,
        weather = state.weather,
        suitabilityScore = state.suitabilityScore,
    )
    state.rainRiskAlert?.let { alert ->
        RainRiskAlertCard(alert = alert)
    }
    ActionRecommendationCard(recommendation = state.recommendation)
    state.hangWindow?.let { window ->
        HangWindowCard(window = window)
    }
    ActiveLoadCard(loadEstimate = state.loadEstimate)
    state.loadEstimate.estimatedCost?.let { cost ->
        EnergyEstimateCard(cost = cost)
    }
    CurrentStatusCard(
        status = state.currentStatus,
        isUpdatingStatus = isUpdatingStatus,
        statusUpdateError = statusUpdateError,
        onAdvanceStatus = onAdvanceActiveLoadStatus,
        onDiscardStatus = onDiscardActiveLoad,
    )
    Text(
        text = stringResource(R.string.dashboard_source, weatherSourceLabelText(state.sourceLabel)),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun GeneralRecommendationCard(
    recommendation: DashboardEmptyRecommendationUiState,
    onCreateNewLoad: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.dashboard_empty_weather_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(
                    R.string.dashboard_empty_weather_message,
                    recommendation.dryingLocation.localizedLabel(),
                    recommendation.estimatedDryingMinutes.toHoursMinutes(),
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onCreateNewLoad) {
                Text(text = stringResource(R.string.dashboard_empty_create_load_action))
            }
        }
    }
}

@Composable
private fun LoadingStateCard() {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.dashboard_loading_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.dashboard_loading_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StateMessageCard(
    title: String,
    message: String,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActionRecommendationCard(
    recommendation: ActionRecommendationUi,
) {
    val statusColors = TenderTheme.statusColors
    val (containerColor, contentColor, accentColor) = when (recommendation.action) {
        DashboardRecommendationAction.START_NOW,
        DashboardRecommendationAction.CHECK_DRYNESS,
        -> Triple(
            statusColors.goodContainer,
            statusColors.onGoodContainer,
            statusColors.good,
        )
        DashboardRecommendationAction.START_WITH_CAUTION,
        DashboardRecommendationAction.PREPARE_TO_HANG,
        DashboardRecommendationAction.MONITOR_DRYING,
        -> Triple(
            statusColors.warnContainer,
            statusColors.onWarnContainer,
            statusColors.warn,
        )
        DashboardRecommendationAction.WAIT_OR_USE_INDOOR -> Triple(
            statusColors.badContainer,
            statusColors.onBadContainer,
            statusColors.bad,
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherGlyph(
                kind = recommendation.action.glyph(),
                tint = accentColor,
                modifier = Modifier.size(42.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.dashboard_next_action_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(recommendation.action.titleRes()),
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(
                        recommendation.action.messageRes(),
                        recommendation.estimatedDryingMinutes.toHoursMinutes(),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                )
            }
        }
    }
}

@Composable
private fun RainRiskAlertCard(alert: RainRiskAlertUi) {
    val statusColors = TenderTheme.statusColors
    val highRisk = alert.severity == RainRiskSeverity.HIGH
    val containerColor = if (highRisk) statusColors.badContainer else statusColors.warnContainer
    val contentColor = if (highRisk) statusColors.onBadContainer else statusColors.onWarnContainer
    val strongColor = if (highRisk) statusColors.bad else statusColors.warn

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherGlyph(
                kind = GlyphKind.RAIN,
                tint = strongColor,
                modifier = Modifier.size(42.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(alert.titleRes()),
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(alert.messageRes(), alert.rainProbabilityPercent),
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                )
            }
        }
    }
}

@Composable
private fun HangWindowCard(window: DryingHangWindowUi) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.dashboard_hang_window_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(
                    R.string.dashboard_hang_window_message,
                    window.startEpochMillis.toTimeLabelOrDash(),
                    window.endEpochMillis.toTimeLabelOrDash(),
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (window.hourlySlots.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.dashboard_hourly_forecast_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(window.hourlySlots) { slot ->
                        HourlyForecastSlot(slot = slot)
                    }
                }
            }
        }
    }
}

@Composable
private fun HourlyForecastSlot(slot: DryingHourlyForecastSlotUi) {
    val statusColors = TenderTheme.statusColors
    val (containerColor, contentColor, accentColor) = when (slot.verdict) {
        DryingVerdict.GOOD -> Triple(
            statusColors.goodContainer,
            statusColors.onGoodContainer,
            statusColors.good,
        )
        DryingVerdict.CAUTION -> Triple(
            statusColors.warnContainer,
            statusColors.onWarnContainer,
            statusColors.warn,
        )
        DryingVerdict.BAD -> Triple(
            statusColors.badContainer,
            statusColors.onBadContainer,
            statusColors.bad,
        )
    }
    val description = stringResource(
        R.string.dashboard_hourly_slot_accessibility,
        slot.forecastForEpochMillis.toTimeLabelOrDash(),
        slot.verdict.localizedLabel(),
        slot.temperature,
        slot.rainProbabilityPercent,
        slot.suitabilityScore,
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        modifier = Modifier
            .width(96.dp)
            .semantics { contentDescription = description },
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Text(
                text = slot.forecastForEpochMillis.toTimeLabelOrDash(),
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
                fontWeight = FontWeight.Bold,
            )
            WeatherGlyph(
                kind = slot.glyph(),
                tint = accentColor,
                modifier = Modifier.size(34.dp),
            )
            Text(
                text = slot.temperature,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.dashboard_hourly_rain_compact_format, slot.rainProbabilityPercent),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
            )
            Text(
                text = stringResource(R.string.dashboard_hourly_score_format, slot.suitabilityScore),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
            )
        }
    }
}

private fun DryingHourlyForecastSlotUi.glyph(): GlyphKind = when (verdict) {
    DryingVerdict.GOOD -> GlyphKind.SUN
    DryingVerdict.CAUTION -> GlyphKind.CLOUD
    DryingVerdict.BAD -> GlyphKind.RAIN
}

@Composable
private fun WeatherVerdictCard(
    verdict: WeatherVerdictUi,
    weather: WeatherMetricsUi,
    suitabilityScore: Int,
) {
    val statusColors = TenderTheme.statusColors
    val containerColor = when (verdict.verdict) {
        DryingVerdict.GOOD -> statusColors.goodContainer
        DryingVerdict.CAUTION -> statusColors.warnContainer
        DryingVerdict.BAD -> statusColors.badContainer
    }
    val contentColor = when (verdict.verdict) {
        DryingVerdict.GOOD -> statusColors.onGoodContainer
        DryingVerdict.CAUTION -> statusColors.onWarnContainer
        DryingVerdict.BAD -> statusColors.onBadContainer
    }
    val strongColor = when (verdict.verdict) {
        DryingVerdict.GOOD -> statusColors.good
        DryingVerdict.CAUTION -> statusColors.warn
        DryingVerdict.BAD -> statusColors.bad
    }
    val glyph = when (verdict.verdict) {
        DryingVerdict.GOOD -> GlyphKind.SUN
        DryingVerdict.CAUTION -> GlyphKind.CLOUD
        DryingVerdict.BAD -> GlyphKind.RAIN
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Temperatura hero + ícono de clima dominante
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = weather.temperature,
                        style = MaterialTheme.typography.displayLarge,
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TonalPill(
                        text = verdict.verdict.localizedLabel(),
                        container = MaterialTheme.colorScheme.surface,
                        contentColor = strongColor,
                        showDot = true,
                    )
                }
                WeatherGlyph(
                    kind = glyph,
                    tint = strongColor,
                    modifier = Modifier.size(76.dp),
                )
            }

            // Veredicto de 3 segundos
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = verdict.verdict.localizedTitle(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = verdict.verdict.localizedReason(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor
                )
            }

            // Métricas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    kind = MetricKind.TEMP,
                    label = stringResource(R.string.dashboard_metric_temp),
                    value = weather.temperature,
                    modifier = Modifier.weight(1f),
                )
                MetricTile(
                    kind = MetricKind.HUMIDITY,
                    label = stringResource(R.string.dashboard_metric_humidity),
                    value = weather.humidity,
                    modifier = Modifier.weight(1f),
                )
                MetricTile(
                    kind = MetricKind.WIND,
                    label = stringResource(R.string.dashboard_metric_wind),
                    value = weather.wind,
                    modifier = Modifier.weight(1f),
                )
            }

            // Gauge de aptitud de secado
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.dashboard_score_format, suitabilityScore),
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor,
                )
                StatBar(
                    progress = suitabilityScore / 100f,
                    color = strongColor,
                    trackColor = contentColor.copy(alpha = 0.15f),
                    height = 10.dp,
                )
            }
        }
    }
}

@Composable
private fun ActiveLoadCard(
    loadEstimate: LoadEstimateUi,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_active_load),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            InfoRow(
                label = stringResource(R.string.dashboard_washer),
                value = loadEstimate.washer ?: stringResource(
                    if (loadEstimate.hasWasherLinked) {
                        R.string.dashboard_unknown_washer
                    } else {
                        R.string.dashboard_no_washer
                    },
                ),
            )
            InfoRow(
                label = stringResource(R.string.dashboard_fabric_type),
                value = loadEstimate.clothingType.localizedLabel(),
            )
            InfoRow(
                label = stringResource(R.string.dashboard_program),
                value = loadEstimate.program.localizedLabel(),
            )
            InfoRow(
                label = stringResource(R.string.dashboard_drying_location),
                value = loadEstimate.dryingLocation.localizedLabel(),
            )

            // Estimación de secado destacada
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_estimated_dry_time, "").trim().trimEnd(':'),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = loadEstimate.estimatedDryingMinutes.toHoursMinutes(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun EnergyEstimateCard(
    cost: EstimatedWashingCost,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            EnergyCostSummary(cost = cost)
        }
    }
}

@Composable
private fun CurrentStatusCard(
    status: CurrentStatusUi,
    isUpdatingStatus: Boolean,
    statusUpdateError: String?,
    onAdvanceStatus: () -> Unit,
    onDiscardStatus: () -> Unit,
) {
    val statusColors = TenderTheme.statusColors
    val (pillContainer, pillContent) = when (status.status) {
        LaundryLoadStatus.COMPLETED -> statusColors.goodContainer to statusColors.onGoodContainer
        LaundryLoadStatus.CANCELLED -> statusColors.badContainer to statusColors.onBadContainer
        else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }
    var currentTimeMillis by remember(status.status, status.dryingStartedAtEpochMillis, status.dryingEstimatedPickupAtEpochMillis) {
        mutableLongStateOf(System.currentTimeMillis())
    }
    LaunchedEffect(status.status, status.dryingStartedAtEpochMillis, status.dryingEstimatedPickupAtEpochMillis) {
        if (status.status == LaundryLoadStatus.DRYING) {
            while (true) {
                currentTimeMillis = System.currentTimeMillis()
                delay(DASHBOARD_STATUS_TICK_MILLIS)
            }
        }
    }
    val liveProgress = status.liveProgress(currentTimeMillis)
    val liveRemainingMinutes = status.liveRemainingDryingMinutes(currentTimeMillis)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.dashboard_current_status),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                TonalPill(
                    text = status.status.localizedLabel(),
                    container = pillContainer,
                    contentColor = pillContent,
                    showDot = true,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatBar(
                    progress = liveProgress,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    height = 10.dp,
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.dashboard_drying_progress_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${(liveProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (status.status == LaundryLoadStatus.DRYING) {
                    Text(
                        text = stringResource(R.string.dashboard_drying_progress_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatusMetricTile(
                    label = stringResource(status.primaryStatusTimingLabelRes()),
                    value = status.primaryStatusTimingValue(),
                    modifier = Modifier.weight(1f),
                )
                StatusMetricTile(
                    label = stringResource(status.nextStatusTimingLabelRes()),
                    value = status.nextStatusTimingValue(liveRemainingMinutes),
                    modifier = Modifier.weight(1f),
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = status.statusGuidanceText(liveRemainingMinutes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(
                            R.string.dashboard_source,
                            weatherSourceLabelText(status.sourceLabel),
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            val actionLabel = status.status.nextActionLabelRes()
            if (actionLabel != null) {
                Button(
                    onClick = onAdvanceStatus,
                    enabled = !isUpdatingStatus,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(actionLabel))
                }
            }
            if (status.status.isActive()) {
                OutlinedButton(
                    onClick = onDiscardStatus,
                    enabled = !isUpdatingStatus,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.laundry_status_action_discard))
                }
            }
            statusUpdateError?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun StatusMetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun DryingVerdict.localizedTitle(): String = when (this) {
    DryingVerdict.GOOD -> stringResource(R.string.dashboard_verdict_good_title)
    DryingVerdict.CAUTION -> stringResource(R.string.dashboard_verdict_caution_title)
    DryingVerdict.BAD -> stringResource(R.string.dashboard_verdict_bad_title)
}

@Composable
private fun DryingVerdict.localizedReason(): String = when (this) {
    DryingVerdict.GOOD -> stringResource(R.string.dashboard_verdict_good_reason)
    DryingVerdict.CAUTION -> stringResource(R.string.dashboard_verdict_caution_reason)
    DryingVerdict.BAD -> stringResource(R.string.dashboard_verdict_bad_reason)
}

@Composable
private fun DryingVerdict.localizedLabel(): String = when (this) {
    DryingVerdict.GOOD -> stringResource(R.string.dashboard_verdict_recommended)
    DryingVerdict.CAUTION -> stringResource(R.string.dashboard_verdict_caution)
    DryingVerdict.BAD -> stringResource(R.string.dashboard_verdict_risk)
}

@Composable
private fun ClothingType.localizedLabel(): String = when (this) {
    ClothingType.LIGHT_CLOTHES -> stringResource(R.string.dashboard_clothing_light)
    ClothingType.HEAVY_CLOTHES -> stringResource(R.string.dashboard_clothing_heavy)
    ClothingType.BEDDING -> stringResource(R.string.dashboard_clothing_bedding)
    ClothingType.DELICATES -> stringResource(R.string.dashboard_clothing_delicates)
    ClothingType.MIXED -> stringResource(R.string.dashboard_clothing_mixed)
}

@Composable
private fun WashingProgram.localizedLabel(): String = when (this) {
    WashingProgram.QUICK -> stringResource(R.string.dashboard_program_quick)
    WashingProgram.NORMAL -> stringResource(R.string.dashboard_program_normal)
    WashingProgram.ECO -> stringResource(R.string.dashboard_program_eco)
    WashingProgram.DELICATE -> stringResource(R.string.dashboard_program_delicate)
}

@Composable
private fun DryingLocation.localizedLabel(): String = when (this) {
    DryingLocation.INDOOR -> stringResource(R.string.new_load_drying_location_indoor)
    DryingLocation.BALCONY -> stringResource(R.string.new_load_drying_location_balcony)
    DryingLocation.OUTDOOR_LINE -> stringResource(R.string.new_load_drying_location_outdoor_line)
    DryingLocation.PATIO -> stringResource(R.string.new_load_drying_location_patio)
    DryingLocation.LAUNDRY_ROOM -> stringResource(R.string.new_load_drying_location_laundry_room)
}

@Composable
private fun LaundryLoadStatus.localizedLabel(): String = when (this) {
    LaundryLoadStatus.PLANNED -> stringResource(R.string.dashboard_status_planned)
    LaundryLoadStatus.WASHING -> stringResource(R.string.dashboard_status_washing)
    LaundryLoadStatus.DRYING -> stringResource(R.string.dashboard_status_drying)
    LaundryLoadStatus.COMPLETED -> stringResource(R.string.dashboard_status_completed)
    LaundryLoadStatus.CANCELLED -> stringResource(R.string.dashboard_status_cancelled)
}

private fun LaundryLoadStatus.nextActionLabelRes(): Int? = when (this) {
    LaundryLoadStatus.PLANNED -> R.string.laundry_status_action_start_washing
    LaundryLoadStatus.WASHING -> R.string.laundry_status_action_mark_drying
    LaundryLoadStatus.DRYING -> R.string.laundry_status_action_mark_completed
    LaundryLoadStatus.COMPLETED,
    LaundryLoadStatus.CANCELLED,
    -> null
}

private fun LaundryLoadStatus.isActive(): Boolean {
    return this != LaundryLoadStatus.COMPLETED && this != LaundryLoadStatus.CANCELLED
}

private fun DashboardRecommendationAction.titleRes(): Int {
    return when (this) {
        DashboardRecommendationAction.START_NOW -> R.string.dashboard_recommendation_start_now_title
        DashboardRecommendationAction.START_WITH_CAUTION -> R.string.dashboard_recommendation_start_caution_title
        DashboardRecommendationAction.WAIT_OR_USE_INDOOR -> R.string.dashboard_recommendation_wait_indoor_title
        DashboardRecommendationAction.PREPARE_TO_HANG -> R.string.dashboard_recommendation_prepare_hang_title
        DashboardRecommendationAction.MONITOR_DRYING -> R.string.dashboard_recommendation_monitor_drying_title
        DashboardRecommendationAction.CHECK_DRYNESS -> R.string.dashboard_recommendation_check_dryness_title
    }
}

private fun DashboardRecommendationAction.messageRes(): Int {
    return when (this) {
        DashboardRecommendationAction.START_NOW -> R.string.dashboard_recommendation_start_now_message
        DashboardRecommendationAction.START_WITH_CAUTION -> R.string.dashboard_recommendation_start_caution_message
        DashboardRecommendationAction.WAIT_OR_USE_INDOOR -> R.string.dashboard_recommendation_wait_indoor_message
        DashboardRecommendationAction.PREPARE_TO_HANG -> R.string.dashboard_recommendation_prepare_hang_message
        DashboardRecommendationAction.MONITOR_DRYING -> R.string.dashboard_recommendation_monitor_drying_message
        DashboardRecommendationAction.CHECK_DRYNESS -> R.string.dashboard_recommendation_check_dryness_message
    }
}

private fun DashboardRecommendationAction.glyph(): GlyphKind {
    return when (this) {
        DashboardRecommendationAction.START_NOW,
        DashboardRecommendationAction.CHECK_DRYNESS,
        -> GlyphKind.SUN
        DashboardRecommendationAction.START_WITH_CAUTION,
        DashboardRecommendationAction.PREPARE_TO_HANG,
        DashboardRecommendationAction.MONITOR_DRYING,
        -> GlyphKind.CLOUD
        DashboardRecommendationAction.WAIT_OR_USE_INDOOR -> GlyphKind.RAIN
    }
}

private fun RainRiskAlertUi.titleRes(): Int {
    return when (severity) {
        RainRiskSeverity.WATCH -> R.string.dashboard_rain_alert_watch_title
        RainRiskSeverity.HIGH -> R.string.dashboard_rain_alert_high_title
    }
}

private fun RainRiskAlertUi.messageRes(): Int {
    return when (severity) {
        RainRiskSeverity.WATCH -> R.string.dashboard_rain_alert_watch_message
        RainRiskSeverity.HIGH -> R.string.dashboard_rain_alert_high_message
    }
}

@Composable
private fun CurrentStatusUi.statusGuidanceText(liveRemainingMinutes: Int): String {
    return when (status) {
        LaundryLoadStatus.DRYING -> stringResource(
            R.string.dashboard_status_drying_guidance,
            liveRemainingMinutes.toHoursMinutes(),
        )
        LaundryLoadStatus.WASHING -> stringResource(
            R.string.dashboard_status_washing_guidance,
            estimatedDryingMinutes.toHoursMinutes(),
        )
        LaundryLoadStatus.PLANNED -> stringResource(
            R.string.dashboard_status_planned_guidance,
            estimatedDryingMinutes.toHoursMinutes(),
        )
        LaundryLoadStatus.COMPLETED -> stringResource(R.string.dashboard_status_completed_guidance)
        LaundryLoadStatus.CANCELLED -> stringResource(R.string.dashboard_status_cancelled_guidance)
    }
}

private fun CurrentStatusUi.primaryStatusTimingLabelRes(): Int {
    return when (status) {
        LaundryLoadStatus.DRYING -> R.string.dashboard_status_hung_at_label
        LaundryLoadStatus.WASHING,
        LaundryLoadStatus.PLANNED,
        LaundryLoadStatus.COMPLETED,
        LaundryLoadStatus.CANCELLED,
        -> R.string.dashboard_status_estimated_total
    }
}

private fun CurrentStatusUi.primaryStatusTimingValue(): String {
    return when (status) {
        LaundryLoadStatus.DRYING -> dryingStartedAtEpochMillis.toTimeLabelOrDash()
        LaundryLoadStatus.WASHING,
        LaundryLoadStatus.PLANNED,
        LaundryLoadStatus.COMPLETED,
        LaundryLoadStatus.CANCELLED,
        -> estimatedDryingMinutes.toHoursMinutes()
    }
}

private fun CurrentStatusUi.nextStatusTimingLabelRes(): Int {
    return when (status) {
        LaundryLoadStatus.DRYING -> R.string.dashboard_status_check_at_label
        LaundryLoadStatus.WASHING,
        LaundryLoadStatus.PLANNED,
        LaundryLoadStatus.COMPLETED,
        LaundryLoadStatus.CANCELLED,
        -> R.string.dashboard_status_next_step_label
    }
}

@Composable
private fun CurrentStatusUi.nextStatusTimingValue(liveRemainingMinutes: Int): String {
    return when (status) {
        LaundryLoadStatus.DRYING -> dryingEstimatedPickupAtEpochMillis
            .toTimeLabelOrDash()
            .takeUnless { it == DASH_TIME_FALLBACK }
            ?: liveRemainingMinutes.toHoursMinutes()
        LaundryLoadStatus.WASHING -> stringResource(R.string.laundry_status_action_mark_drying)
        LaundryLoadStatus.PLANNED -> stringResource(R.string.laundry_status_action_start_washing)
        LaundryLoadStatus.COMPLETED -> stringResource(R.string.dashboard_status_completed)
        LaundryLoadStatus.CANCELLED -> stringResource(R.string.dashboard_status_cancelled)
    }
}

private fun CurrentStatusUi.liveProgress(currentTimeMillis: Long): Float {
    if (status != LaundryLoadStatus.DRYING || estimatedDryingMinutes <= 0) {
        return progress
    }

    val startedAt = dryingStartedAtEpochMillis ?: return progress
    val estimatedMillis = estimatedDryingMinutes * MILLIS_PER_MINUTE
    val elapsedMillis = (currentTimeMillis - startedAt).coerceAtLeast(0L)

    return (elapsedMillis.toDouble() / estimatedMillis.toDouble())
        .toFloat()
        .coerceIn(0f, MAX_ACTIVE_DRYING_PROGRESS)
}

private fun CurrentStatusUi.liveRemainingDryingMinutes(currentTimeMillis: Long): Int {
    if (status != LaundryLoadStatus.DRYING) {
        val remainingRatio = 1f - progress.coerceIn(0f, 1f)
        return ceil(estimatedDryingMinutes * remainingRatio.toDouble())
            .toInt()
            .coerceAtLeast(0)
    }

    dryingEstimatedPickupAtEpochMillis?.let { pickupAt ->
        return ceil((pickupAt - currentTimeMillis).toDouble() / MILLIS_PER_MINUTE.toDouble())
            .toInt()
            .coerceAtLeast(0)
    }

    val remainingRatio = 1f - liveProgress(currentTimeMillis).coerceIn(0f, 1f)
    return ceil(estimatedDryingMinutes * remainingRatio.toDouble())
        .toInt()
        .coerceAtLeast(0)
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

private fun Long?.toTimeLabelOrDash(): String {
    return this?.let { epochMillis ->
        DASHBOARD_TIME_FORMATTER.format(
            Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()),
        )
    } ?: DASH_TIME_FALLBACK
}

private const val DASHBOARD_STATUS_TICK_MILLIS = 60_000L
private const val MILLIS_PER_MINUTE = 60_000L
private const val MAX_ACTIVE_DRYING_PROGRESS = 0.99f
private const val DASH_TIME_FALLBACK = "--"
private val DASHBOARD_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
private fun weatherSourceLabelText(sourceLabel: String): String {
    return when (sourceLabel) {
        "Open-Meteo" -> stringResource(R.string.dashboard_source_open_meteo)
        "MET Norway" -> stringResource(R.string.dashboard_source_met_no)
        "Mock backend" -> stringResource(R.string.dashboard_source_mock_backend)
        "Backend fallback" -> stringResource(R.string.dashboard_source_backend_fallback)
        "Fallback local" -> stringResource(R.string.dashboard_source_local_fallback)
        else -> sourceLabel
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

private fun sampleDashboard() = DashboardUiState(
    activeLoadId = "load-preview",
    verdict = WeatherVerdictUi(
        verdict = DryingVerdict.GOOD,
        reason = "Sol y viento moderado: condiciones ideales para secar al aire libre.",
    ),
    recommendation = ActionRecommendationUi(
        action = DashboardRecommendationAction.START_NOW,
        estimatedDryingMinutes = 195,
    ),
    rainRiskAlert = null,
    weather = WeatherMetricsUi(temperature = "24 C", humidity = "48%", wind = "18 km/h"),
    loadEstimate = LoadEstimateUi(
        washer = "Lavarropas Principal",
        hasWasherLinked = true,
        clothingType = ClothingType.BEDDING,
        program = WashingProgram.NORMAL,
        dryingLocation = DryingLocation.PATIO,
        estimatedDryingMinutes = 195,
        estimatedCost = EstimatedWashingCost(
            amount = null,
            currency = null,
            level = EstimatedWashingCostLevel.MEDIUM,
            confidence = EstimatedWashingCostConfidence.MEDIUM,
            estimatedEnergyKwh = 0.84,
            estimatedWaterLiters = 52.0,
        ),
    ),
    currentStatus = CurrentStatusUi(
        status = LaundryLoadStatus.DRYING,
        estimatedDryingMinutes = 195,
        progress = 0.62f,
        sourceLabel = "Backend",
    ),
    hangWindow = DryingHangWindowUi(
        startEpochMillis = 1_767_642_000_000L,
        endEpochMillis = 1_767_649_200_000L,
        hourlySlots = listOf(
            DryingHourlyForecastSlotUi(
                forecastForEpochMillis = 1_767_642_000_000L,
                verdict = DryingVerdict.GOOD,
                suitabilityScore = 82,
                temperature = "24 C",
                rainProbabilityPercent = 8,
            ),
            DryingHourlyForecastSlotUi(
                forecastForEpochMillis = 1_767_645_600_000L,
                verdict = DryingVerdict.GOOD,
                suitabilityScore = 79,
                temperature = "23 C",
                rainProbabilityPercent = 12,
            ),
            DryingHourlyForecastSlotUi(
                forecastForEpochMillis = 1_767_649_200_000L,
                verdict = DryingVerdict.CAUTION,
                suitabilityScore = 62,
                temperature = "21 C",
                rainProbabilityPercent = 25,
            ),
        ),
    ),
    suitabilityScore = 82,
    sourceLabel = "Backend",
)

@Preview(showBackground = true, name = "Dashboard — claro")
@Composable
private fun DashboardPreviewLight() {
    TenderAppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardScreenContent(DashboardScreenState.Content(sampleDashboard()))
        }
    }
}

@Preview(
    showBackground = true,
    name = "Dashboard — oscuro",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun DashboardPreviewDark() {
    TenderAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardScreenContent(DashboardScreenState.Content(sampleDashboard()))
        }
    }
}
