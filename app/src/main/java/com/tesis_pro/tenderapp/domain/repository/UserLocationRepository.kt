package com.tesis_pro.tenderapp.domain.repository

import com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType
import com.tesis_pro.tenderapp.domain.model.WeatherLocation

interface UserLocationRepository {
    suspend fun saveHouseholdLocation(location: WeatherLocation): UserLocationSaveResult
}

sealed interface UserLocationSaveResult {
    data object Success : UserLocationSaveResult

    data class Error(
        val type: RemoteDataErrorType,
        val message: String,
    ) : UserLocationSaveResult
}
