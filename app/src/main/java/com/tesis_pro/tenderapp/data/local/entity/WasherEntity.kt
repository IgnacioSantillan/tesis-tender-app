package com.tesis_pro.tenderapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "washers")
data class WasherEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val capacityKg: Double?,
    val energyLabel: String?,
    val waterUsageLiters: Double?,
    val isPrimary: Boolean,
)
