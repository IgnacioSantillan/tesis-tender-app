package com.tesis_pro.tenderapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tesis_pro.tenderapp.R
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCost
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostConfidence
import com.tesis_pro.tenderapp.domain.model.EstimatedWashingCostLevel

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun EnergyCostSummary(
    cost: EstimatedWashingCost,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.energy_cost_summary_title),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            cost.level?.let { level ->
                TonalPill(
                    text = stringResource(R.string.energy_cost_level_format, stringResource(level.labelRes())),
                    container = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    showDot = true,
                )
            }
            cost.estimatedEnergyKwh?.let { energy ->
                TonalPill(
                    text = stringResource(R.string.energy_cost_kwh_format, energy),
                    container = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
            cost.estimatedWaterLiters?.let { water ->
                TonalPill(
                    text = stringResource(R.string.energy_cost_water_format, water),
                    container = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
            cost.confidence?.let { confidence ->
                TonalPill(
                    text = stringResource(
                        R.string.energy_cost_confidence_format,
                        stringResource(confidence.labelRes()),
                    ),
                    container = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Text(
            text = stringResource(R.string.energy_cost_factor_note),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

private fun EstimatedWashingCostLevel.labelRes(): Int {
    return when (this) {
        EstimatedWashingCostLevel.LOW -> R.string.energy_cost_level_low
        EstimatedWashingCostLevel.MEDIUM -> R.string.energy_cost_level_medium
        EstimatedWashingCostLevel.HIGH -> R.string.energy_cost_level_high
        EstimatedWashingCostLevel.UNKNOWN -> R.string.energy_cost_level_unknown
    }
}

private fun EstimatedWashingCostConfidence.labelRes(): Int {
    return when (this) {
        EstimatedWashingCostConfidence.LOW -> R.string.energy_cost_confidence_low
        EstimatedWashingCostConfidence.MEDIUM -> R.string.energy_cost_confidence_medium
        EstimatedWashingCostConfidence.HIGH -> R.string.energy_cost_confidence_high
    }
}
