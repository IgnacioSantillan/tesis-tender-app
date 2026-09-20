package com.tesis_pro.tenderapp.domain.model

data class WeatherSnapshot(
    val capturedAtEpochMillis: Long,
    val forecastForEpochMillis: Long,
    val location: WeatherLocation,
    val condition: WeatherCondition,
    val temperatureCelsius: Double,
    val humidityPercent: Int,
    val windSpeedKph: Double,
    val rainProbabilityPercent: Int,
    val cloudCoverPercent: Int,
    val source: WeatherDataSource = WeatherDataSource.UNKNOWN,
    val isStale: Boolean = false,
)

enum class WeatherDataSource {
    OPEN_METEO,
    MET_NO,
    MOCK,
    LOCAL_FALLBACK,
    UNKNOWN,
}
