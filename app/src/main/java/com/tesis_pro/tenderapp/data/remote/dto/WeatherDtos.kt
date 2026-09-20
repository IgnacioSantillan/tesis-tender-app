package com.tesis_pro.tenderapp.data.remote.dto

data class WeatherLocationDto(
    val id: String,
    val label: String,
    val latitude: Double?,
    val longitude: Double?,
)

data class WeatherSnapshotResponseDto(
    val location: WeatherLocationDto,
    val source: String = "UNKNOWN",
    val capturedAt: String,
    val forecastFor: String,
    val condition: String,
    val temperatureCelsius: Double,
    val humidityPercent: Int,
    val windSpeedKph: Double,
    val rainProbabilityPercent: Int,
    val cloudCoverPercent: Int,
    val isStale: Boolean,
)
