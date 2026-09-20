package com.tesis_pro.tenderapp.domain.repository

import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.model.WeatherSnapshot
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun observeLatestSnapshot(location: WeatherLocation): Flow<WeatherSnapshot?>

    suspend fun refreshForecast(location: WeatherLocation): WeatherSnapshot
}
