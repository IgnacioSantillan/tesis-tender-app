package com.tesis_pro.tenderapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tesis_pro.tenderapp.R
import com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.ui.components.TonalPill
import com.tesis_pro.tenderapp.ui.theme.TenderTheme
import java.text.DateFormat
import java.util.Date

data class HistoryLoadUi(
    val id: String,
    val title: String,
    val dateEpochMillis: Long,
    val status: LaundryLoadStatus,
    val program: String,
    val location: String,
    val isActive: Boolean,
)

@Composable
fun HistoryScreen() {
    val context = LocalContext.current.applicationContext
    val viewModel = remember(context) { HistoryViewModel.createDefault(context) }
    val state by viewModel.uiState.collectAsState()

    HistoryScreenContent(
        state = state,
        onRetry = viewModel::refresh,
        onAdvanceStatus = viewModel::advanceLoadStatus,
        onDiscardLoad = viewModel::discardLoad,
    )
}

@Composable
private fun HistoryScreenContent(
    state: HistoryScreenState,
    onRetry: () -> Unit,
    onAdvanceStatus: (String, LaundryLoadStatus) -> Unit,
    onDiscardLoad: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.history_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.history_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when (state) {
            HistoryScreenState.Empty -> EmptyHistoryCard()
            is HistoryScreenState.Error -> HistoryErrorCard(
                message = state.localizedMessage(),
                onRetry = onRetry,
            )

            HistoryScreenState.Loading -> HistoryLoadingCard()
            is HistoryScreenState.Content -> {
                state.mutationError?.let { error ->
                    HistoryInlineErrorCard(message = error.localizedMessage())
                }
                HistoryLoadSections(
                    loads = state.loads,
                    isUpdating = state.isUpdating,
                    onAdvanceStatus = onAdvanceStatus,
                    onDiscardLoad = onDiscardLoad,
                )
            }
        }
    }
}

@Composable
private fun HistoryLoadSections(
    loads: List<HistoryLoadUi>,
    isUpdating: Boolean,
    onAdvanceStatus: (String, LaundryLoadStatus) -> Unit,
    onDiscardLoad: (String) -> Unit,
) {
    val activeLoads = loads.filter { it.isActive }
    val pastLoads = loads.filterNot { it.isActive }

    if (activeLoads.isNotEmpty()) {
        HistorySectionTitle(text = stringResource(R.string.history_active_section))
        activeLoads.forEach { load ->
            HistoryLoadCard(
                load = load,
                isUpdating = isUpdating,
                onAdvanceStatus = onAdvanceStatus,
                onDiscardLoad = onDiscardLoad,
            )
        }
    }
    if (pastLoads.isNotEmpty()) {
        HistorySectionTitle(text = stringResource(R.string.history_past_section))
        pastLoads.forEach { load ->
            HistoryLoadCard(
                load = load,
                isUpdating = isUpdating,
                onAdvanceStatus = onAdvanceStatus,
                onDiscardLoad = onDiscardLoad,
            )
        }
    }
}

@Composable
private fun HistorySectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun HistoryLoadingCard() {
    SurfaceCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.history_loading_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.history_loading_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HistoryErrorCard(
    message: String,
    onRetry: () -> Unit,
) {
    SurfaceCard {
        Text(
            text = stringResource(R.string.history_error_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.history_retry))
        }
    }
}

@Composable
private fun HistoryInlineErrorCard(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = TenderTheme.statusColors.badContainer,
        contentColor = TenderTheme.statusColors.onBadContainer,
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun EmptyHistoryCard() {
    SurfaceCard {
        Text(
            text = stringResource(R.string.history_empty_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.history_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HistoryLoadCard(
    load: HistoryLoadUi,
    isUpdating: Boolean,
    onAdvanceStatus: (String, LaundryLoadStatus) -> Unit,
    onDiscardLoad: (String) -> Unit,
) {
    val statusColors = TenderTheme.statusColors
    val (pillContainer, pillContent) = when {
        load.isActive -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        load.status == LaundryLoadStatus.CANCELLED -> statusColors.badContainer to statusColors.onBadContainer
        else -> statusColors.goodContainer to statusColors.onGoodContainer
    }
    SurfaceCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = load.title.localizedHistoryTitle(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = load.dateEpochMillis.formatHistoryDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TonalPill(
                text = load.status.localizedLabel(),
                container = pillContainer,
                contentColor = pillContent,
                showDot = true,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            InfoChip(
                text = stringResource(R.string.history_program_format, load.program.localizedProgramLabel()),
                modifier = Modifier.weight(1f),
            )
            InfoChip(
                text = stringResource(R.string.history_location_format, load.location),
                modifier = Modifier.weight(1f),
            )
        }
        val actionLabel = load.status.nextActionLabelRes()
        if (load.isActive && actionLabel != null) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onAdvanceStatus(load.id, load.status) },
                    enabled = !isUpdating,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    actionLabel?.let { Text(text = stringResource(it)) }
                }
                OutlinedButton(
                    onClick = { onDiscardLoad(load.id) },
                    enabled = !isUpdating,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.laundry_status_action_discard))
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun SurfaceCard(
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
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

@Composable
private fun String.localizedHistoryTitle(): String = when (this) {
    "LIGHT_CLOTHES" -> stringResource(R.string.dashboard_clothing_light)
    "HEAVY_CLOTHES" -> stringResource(R.string.dashboard_clothing_heavy)
    "BEDDING" -> stringResource(R.string.dashboard_clothing_bedding)
    "DELICATES" -> stringResource(R.string.dashboard_clothing_delicates)
    "MIXED" -> stringResource(R.string.dashboard_clothing_mixed)
    else -> this
}

@Composable
private fun String.localizedProgramLabel(): String = when (this) {
    "QUICK" -> stringResource(R.string.dashboard_program_quick)
    "NORMAL" -> stringResource(R.string.dashboard_program_normal)
    "ECO" -> stringResource(R.string.dashboard_program_eco)
    "DELICATE" -> stringResource(R.string.dashboard_program_delicate)
    else -> this
}

private fun Long.formatHistoryDate(): String {
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(this))
}

@Composable
private fun HistoryScreenState.Error.localizedMessage(): String {
    return when (type) {
        RemoteDataErrorType.AUTHENTICATION -> stringResource(R.string.history_error_authentication)
        RemoteDataErrorType.NETWORK -> stringResource(R.string.history_error_network)
        RemoteDataErrorType.SERVER -> stringResource(R.string.history_error_server)
        RemoteDataErrorType.INVALID_RESPONSE -> stringResource(R.string.history_error_invalid_response)
        RemoteDataErrorType.NOT_FOUND -> stringResource(R.string.history_error_not_found)
        RemoteDataErrorType.UNKNOWN -> fallbackMessage
    }
}

@Composable
private fun HistoryMutationError.localizedMessage(): String {
    return when (type) {
        RemoteDataErrorType.AUTHENTICATION -> stringResource(R.string.history_error_authentication)
        RemoteDataErrorType.NETWORK -> stringResource(R.string.history_error_network)
        RemoteDataErrorType.SERVER -> stringResource(R.string.history_error_server)
        RemoteDataErrorType.INVALID_RESPONSE -> stringResource(R.string.history_error_invalid_response)
        RemoteDataErrorType.NOT_FOUND -> stringResource(R.string.history_error_not_found)
        RemoteDataErrorType.UNKNOWN -> fallbackMessage
    }
}
