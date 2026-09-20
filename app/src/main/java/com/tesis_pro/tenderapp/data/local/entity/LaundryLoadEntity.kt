package com.tesis_pro.tenderapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laundry_loads")
data class LaundryLoadEntity(
    @PrimaryKey val id: String,
    val washerId: String?,
    val clothingType: String,
    val washingProgram: String,
    val createdAtEpochMillis: Long,
    val completedAtEpochMillis: Long?,
)
