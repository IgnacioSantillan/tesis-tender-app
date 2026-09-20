package com.tesis_pro.tenderapp.data.remote.dto

data class CreateLaundryLoadRequestDto(
    val washerId: String?,
    val clothingType: String,
    val washingProgram: String,
    val locationId: String,
    val dryingLocationId: String? = null,
    val spinRpm: Int? = null,
    val loadSize: String? = null,
)

data class UpdateLaundryLoadStatusRequestDto(
    val status: String,
)

data class LaundryLoadResponseDto(
    val id: String,
    val washerId: String?,
    val clothingType: String,
    val washingProgram: String,
    val status: String,
    val locationId: String,
    val dryingLocationId: String? = null,
    val spinRpm: Int? = null,
    val loadSize: String? = null,
    val estimatedWashingEnergyKwh: Double? = null,
    val estimatedWashingWaterLiters: Double? = null,
    val estimatedWashingCostAmount: Double? = null,
    val estimatedWashingCostCurrency: String? = null,
    val estimatedWashingCostLevel: String? = null,
    val estimatedWashingCostConfidence: String? = null,
    val createdAt: String,
    val startedAt: String?,
    val dryingStartedAt: String? = null,
    val dryingEstimatedMinutesAtStart: Int? = null,
    val dryingEstimatedPickupAt: String? = null,
    val completedAt: String?,
    val prediction: Any?,
)
