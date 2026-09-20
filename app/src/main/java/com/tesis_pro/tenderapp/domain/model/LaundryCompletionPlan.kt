package com.tesis_pro.tenderapp.domain.model

data class LaundryCompletionPlan(
    val generatedAtEpochMillis: Long,
    val plannedStartAtEpochMillis: Long,
    val targetReadyAtEpochMillis: Long,
    val weatherSource: WeatherDataSource,
    val isStale: Boolean,
    val forecastCoverageEndsAtEpochMillis: Long?,
    val recommendedPrograms: List<WashingProgram>,
    val options: List<ProgramCompletionOption>,
)

data class ProgramCompletionOption(
    val program: WashingProgram,
    val washingMinutes: Int,
    val washingEndsAtEpochMillis: Long,
    val dryingStartsAtEpochMillis: Long,
    val estimatedDryingMinutes: Int,
    val estimatedReadyAtEpochMillis: Long,
    val totalElapsedMinutes: Int,
    val marginMinutes: Int,
    val feasible: Boolean,
    val usesForecastExtrapolation: Boolean,
    val verdict: DryingVerdict,
    val suitabilityScore: Int,
)
