package com.tesis_pro.tenderapp.domain.model

data class LaundryLoad(
    val id: String,
    val washerId: String?,
    val clothingType: ClothingType,
    val washingProgram: WashingProgram,
    val status: LaundryLoadStatus,
    val location: WeatherLocation,
    val dryingLocation: DryingLocation = DryingLocation.PATIO,
    val createdAtEpochMillis: Long,
    val startedAtEpochMillis: Long?,
    val completedAtEpochMillis: Long?,
    val prediction: DryingPrediction?,
    val dryingStartedAtEpochMillis: Long? = null,
    val dryingEstimatedMinutesAtStart: Int? = null,
    val dryingEstimatedPickupAtEpochMillis: Long? = null,
    val spinRpm: SpinSpeedRpm? = null,
    val loadSize: LoadSize? = null,
    val estimatedWashingCost: EstimatedWashingCost? = null,
)
