package com.tesis_pro.tenderapp.domain.model

data class Washer(
    val id: String,
    val name: String,
    val type: WasherType,
    val capacityKg: Double?,
    val energyLabel: String?,
    val waterUsageLiters: Double?,
    val isPrimary: Boolean,
    val defaultSpinRpm: SpinSpeedRpm? = null,
)
