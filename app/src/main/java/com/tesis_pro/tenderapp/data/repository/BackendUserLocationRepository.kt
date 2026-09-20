package com.tesis_pro.tenderapp.data.repository

import com.tesis_pro.tenderapp.data.remote.RemoteDataResult
import com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType
import com.tesis_pro.tenderapp.data.remote.RemoteUserLocationDataSource
import com.tesis_pro.tenderapp.data.remote.dto.SaveUserLocationRequestDto
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.repository.UserLocationRepository
import com.tesis_pro.tenderapp.domain.repository.UserLocationSaveResult

class BackendUserLocationRepository(
    private val dataSource: RemoteUserLocationDataSource,
    private val accessTokenProvider: () -> String?,
) : UserLocationRepository {
    override suspend fun saveHouseholdLocation(location: WeatherLocation): UserLocationSaveResult {
        val accessToken = accessTokenProvider()?.trim().orEmpty()
        if (accessToken.isEmpty()) {
            return UserLocationSaveResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Authentication is required to save household location.",
            )
        }

        val request = SaveUserLocationRequestDto(
            locationId = location.id,
            label = location.label,
            latitude = location.latitude,
            longitude = location.longitude,
        )

        return when (val result = dataSource.saveUserLocation(accessToken, request)) {
            is RemoteDataResult.Success -> UserLocationSaveResult.Success
            is RemoteDataResult.Error -> UserLocationSaveResult.Error(result.type, result.message)
        }
    }
}
