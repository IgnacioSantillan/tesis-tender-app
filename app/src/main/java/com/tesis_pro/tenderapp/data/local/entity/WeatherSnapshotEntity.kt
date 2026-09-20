package com.tesis_pro.tenderapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_snapshots")
data class WeatherSnapshotEntity(
    @PrimaryKey val id: String,
    val locationId: String,
    val locationLabel: String,
    val latitude: Double?,
    val longitude: Double?,
    val capturedAtEpochMillis: Long,
    val forecastForEpochMillis: Long,
    val condition: String,
    val temperatureCelsius: Double,
    val humidityPercent: Int,
    val windSpeedKph: Double,
    val rainProbabilityPercent: Int,
    val cloudCoverPercent: Int,
)
