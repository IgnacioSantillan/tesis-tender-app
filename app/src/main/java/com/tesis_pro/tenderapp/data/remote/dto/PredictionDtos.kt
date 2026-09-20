package com.tesis_pro.tenderapp.data.remote.dto

data class CreateDryingPredictionRequestDto(
    val laundryLoadId: String,
    val clothingType: String,
    val washingProgram: String,
    val dryingMethod: String,
    val locationId: String,
    val dryingLocationId: String? = null,
    val spinRpm: Int? = null,
    val loadSize: String? = null,
    val washerEnergyLabel: String? = null,
    val washerCapacityKg: Double? = null,
    val waterUsageLiters: Double? = null,
)

data class CreateCompletionPlanRequestDto(
    val laundryLoadId: String,
    val clothingType: String,
    val dryingMethod: String,
    val locationId: String,
    val dryingLocationId: String,
    val spinRpm: Int? = null,
    val loadSize: String? = null,
    val washerEnergyLabel: String? = null,
    val washerCapacityKg: Double? = null,
    val waterUsageLiters: Double? = null,
    val plannedStartAt: String,
    val targetReadyAt: String,
)

data class LaundryCompletionPlanResponseDto(
    val generatedAt: String,
    val plannedStartAt: String,
    val targetReadyAt: String,
    val weatherSource: String,
    val isStale: Boolean,
    val forecastCoverageEndsAt: String? = null,
    val recommendedPrograms: List<String> = emptyList(),
    val options: List<ProgramCompletionOptionResponseDto> = emptyList(),
)

data class ProgramCompletionOptionResponseDto(
    val program: String,
    val washingMinutes: Int,
    val washingEndsAt: String,
    val dryingStartsAt: String,
    val estimatedDryingMinutes: Int,
    val estimatedReadyAt: String,
    val totalElapsedMinutes: Int,
    val marginMinutes: Int,
    val feasible: Boolean,
    val usesForecastExtrapolation: Boolean,
    val verdict: String,
    val suitabilityScore: Int,
)

data class EstimatedWashingCostResponseDto(
    val amount: Double? = null,
    val currency: String? = null,
    val level: String? = null,
    val confidence: String? = null,
    val estimatedEnergyKwh: Double? = null,
    val estimatedWaterLiters: Double? = null,
)

data class DryingPredictionResponseDto(
    val verdict: String,
    val dryingMethod: String,
    val dryingLocationId: String,
    val dryingLocationLabel: String,
    val estimatedDryingMinutes: Int,
    val recommendedHangAt: String,
    val recommendedHangWindowStart: String? = null,
    val recommendedHangWindowEnd: String? = null,
    val estimatedPickupAt: String,
    val suitabilityScore: Int,
    val reason: String,
    val weatherSnapshot: WeatherSnapshotResponseDto,
    val estimatedCost: EstimatedWashingCostResponseDto? = null,
    val hourlySlots: List<DryingHourlySlotResponseDto> = emptyList(),
)

data class DryingHourlySlotResponseDto(
    val forecastFor: String,
    val verdict: String,
    val suitabilityScore: Int,
    val temperatureCelsius: Double,
    val humidityPercent: Int,
    val windSpeedKph: Double,
    val rainProbabilityPercent: Int,
)
