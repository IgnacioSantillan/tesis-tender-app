package com.tesis_pro.tenderapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SaveWasherRequestDto(
    val name: String,
    val type: String,
    val capacityKg: Double?,
    val energyLabel: String?,
    val waterUsageLiters: Double?,
    val defaultSpinRpm: Int? = null,
    val isPrimary: Boolean,
)

data class WasherResponseDto(
    val id: String,
    val name: String,
    val type: String,
    @SerializedName(value = "capacityKg", alternate = ["capacity_kg"])
    val capacityKg: Double?,
    @SerializedName(value = "energyLabel", alternate = ["energy_label"])
    val energyLabel: String?,
    @SerializedName(value = "waterUsageLiters", alternate = ["water_usage_liters"])
    val waterUsageLiters: Double?,
    @SerializedName(value = "defaultSpinRpm", alternate = ["default_spin_rpm"])
    val defaultSpinRpm: Int? = null,
    @SerializedName(value = "isPrimary", alternate = ["is_primary"])
    val isPrimary: Boolean,
)
