package com.tesis_pro.tenderapp.domain.repository

import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.model.DryingLocation

interface HouseholdSettingsRepository {
    fun getWeatherLocation(): WeatherLocation

    fun getDefaultDryingLocation(): DryingLocation = DryingLocation.PATIO

    fun saveWeatherLocation(location: WeatherLocation) = Unit

    fun saveDefaultDryingLocation(location: DryingLocation) = Unit
}
