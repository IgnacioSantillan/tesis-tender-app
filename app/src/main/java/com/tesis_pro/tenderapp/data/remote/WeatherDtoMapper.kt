package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.WeatherSnapshotResponseDto
import com.tesis_pro.tenderapp.domain.model.WeatherDataSource
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.model.WeatherSnapshot
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

internal fun WeatherSnapshotResponseDto.toDomain(): WeatherSnapshot {
    return WeatherSnapshot(
        capturedAtEpochMillis = capturedAt.toEpochMillis(),
        forecastForEpochMillis = forecastFor.toEpochMillis(),
        location = WeatherLocation(
            id = location.id,
            label = location.label,
            latitude = location.latitude,
            longitude = location.longitude,
        ),
        condition = enumValueOf(condition),
        temperatureCelsius = temperatureCelsius,
        humidityPercent = humidityPercent,
        windSpeedKph = windSpeedKph,
        rainProbabilityPercent = rainProbabilityPercent,
        cloudCoverPercent = cloudCoverPercent,
        source = source.toWeatherDataSource(),
        isStale = isStale,
    )
}

private fun String.toWeatherDataSource(): WeatherDataSource {
    return runCatching {
        enumValueOf<WeatherDataSource>(uppercase().replace("-", "_"))
    }.getOrDefault(WeatherDataSource.UNKNOWN)
}

internal fun String.toEpochMillis(): Long {
    return WeatherIsoUtcDateFormat.parse(this)
}

private object WeatherIsoUtcDateFormat {
    private val formats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
    ).map { pattern ->
        SimpleDateFormat(pattern, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    fun parse(value: String): Long {
        formats.forEach { format ->
            try {
                return format.parse(value)?.time ?: throw ParseException(value, 0)
            } catch (exception: ParseException) {
                // Try the next supported backend timestamp shape.
            }
        }

        throw ParseException(value, 0)
    }
}
