package com.tesis_pro.tenderapp.data.repository

import android.content.Context
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.repository.HouseholdSettingsRepository

class LocalHouseholdSettingsRepository(
    context: Context? = null,
) : HouseholdSettingsRepository {
    private val preferences = context?.applicationContext?.getSharedPreferences(
        PreferencesName,
        Context.MODE_PRIVATE,
    )

    override fun getWeatherLocation(): WeatherLocation {
        val prefs = preferences ?: return DefaultWeatherLocation
        return WeatherLocation(
            id = prefs.getString(KeyLocationId, DefaultWeatherLocation.id).orEmpty()
                .ifBlank { DefaultWeatherLocation.id },
            label = prefs.getString(KeyLocationLabel, DefaultWeatherLocation.label).orEmpty()
                .ifBlank { DefaultWeatherLocation.label },
            latitude = prefs.getString(KeyLatitude, null)?.toDoubleOrNull(),
            longitude = prefs.getString(KeyLongitude, null)?.toDoubleOrNull(),
        )
    }

    override fun getDefaultDryingLocation(): DryingLocation {
        val rawValue = preferences?.getString(KeyDefaultDryingLocation, null)
        return runCatching {
            enumValueOf<DryingLocation>(rawValue.orEmpty())
        }.getOrDefault(DefaultDryingLocation)
    }

    override fun saveWeatherLocation(location: WeatherLocation) {
        preferences?.edit()
            ?.putString(KeyLocationId, location.id)
            ?.putString(KeyLocationLabel, location.label)
            ?.putString(KeyLatitude, location.latitude?.toString())
            ?.putString(KeyLongitude, location.longitude?.toString())
            ?.apply()
    }

    override fun saveDefaultDryingLocation(location: DryingLocation) {
        preferences?.edit()
            ?.putString(KeyDefaultDryingLocation, location.name)
            ?.apply()
    }

    companion object {
        private const val PreferencesName = "household_settings"
        private const val KeyLocationId = "location_id"
        private const val KeyLocationLabel = "location_label"
        private const val KeyLatitude = "latitude"
        private const val KeyLongitude = "longitude"
        private const val KeyDefaultDryingLocation = "default_drying_location"

        val DefaultDryingLocation = DryingLocation.PATIO

        val DefaultWeatherLocation = WeatherLocation(
            id = "home",
            label = "Home patio",
            latitude = null,
            longitude = null,
        )
    }
}
