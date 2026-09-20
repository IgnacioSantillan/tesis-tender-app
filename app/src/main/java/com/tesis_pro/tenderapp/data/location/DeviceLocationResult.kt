package com.tesis_pro.tenderapp.data.location

data class DeviceLocation(
    val latitude: Double,
    val longitude: Double,
)

sealed interface DeviceLocationResult {
    data class Success(val location: DeviceLocation) : DeviceLocationResult

    data object PermissionDenied : DeviceLocationResult

    data object Unavailable : DeviceLocationResult
}
