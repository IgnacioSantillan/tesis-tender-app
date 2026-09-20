package com.tesis_pro.tenderapp.domain.model

data class DryingPrediction(
    val verdict: DryingVerdict,
    val method: DryingMethod,
    val estimatedDryingMinutes: Int,
    val recommendedHangAtEpochMillis: Long,
    val recommendedHangWindowStartEpochMillis: Long = recommendedHangAtEpochMillis,
    val recommendedHangWindowEndEpochMillis: Long = recommendedHangAtEpochMillis,
    val estimatedPickupAtEpochMillis: Long,
    val suitabilityScore: Int,
    val reason: String,
    val weatherSnapshot: WeatherSnapshot,
    val estimatedCost: EstimatedWashingCost? = null,
    val hourlySlots: List<DryingHourlySlot> = emptyList(),
)

data class DryingHourlySlot(
    val forecastForEpochMillis: Long,
    val verdict: DryingVerdict,
    val suitabilityScore: Int,
    val temperatureCelsius: Double,
    val humidityPercent: Int,
    val windSpeedKph: Double,
    val rainProbabilityPercent: Int,
)
