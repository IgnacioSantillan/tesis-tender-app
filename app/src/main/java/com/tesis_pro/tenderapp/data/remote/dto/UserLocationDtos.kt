package com.tesis_pro.tenderapp.data.remote.dto

data class SaveUserLocationRequestDto(
    val locationId: String,
    val label: String,
    val latitude: Double?,
    val longitude: Double?,
)

data class UserLocationResponseDto(
    val id: String,
    val label: String,
    val latitude: Double?,
    val longitude: Double?,
    val isPrimary: Boolean,
    val updatedAt: String,
)
