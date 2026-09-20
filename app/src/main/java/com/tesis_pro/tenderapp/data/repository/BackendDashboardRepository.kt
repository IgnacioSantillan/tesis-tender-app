package com.tesis_pro.tenderapp.data.repository

import com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType
import com.tesis_pro.tenderapp.data.remote.RemoteDataResult
import com.tesis_pro.tenderapp.data.remote.CreateRemoteDryingPrediction
import com.tesis_pro.tenderapp.data.remote.RemoteLaundryDataSource
import com.tesis_pro.tenderapp.data.remote.RemotePredictionDataSource
import com.tesis_pro.tenderapp.data.remote.RemoteWeatherDataSource
import com.tesis_pro.tenderapp.data.remote.TenderBackendApi
import com.tesis_pro.tenderapp.domain.model.DryingLocation
import com.tesis_pro.tenderapp.domain.model.DryingMethod
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.repository.DashboardLoadResult
import com.tesis_pro.tenderapp.domain.repository.DashboardRepository
import com.tesis_pro.tenderapp.domain.repository.HouseholdSettingsRepository
import java.io.IOException
import retrofit2.HttpException

class BackendDashboardRepository(
    private val api: TenderBackendApi,
    private val laundryDataSource: RemoteLaundryDataSource,
    private val weatherDataSource: RemoteWeatherDataSource,
    private val predictionDataSource: RemotePredictionDataSource? = null,
    private val householdSettingsRepository: HouseholdSettingsRepository,
    private val accessTokenProvider: () -> String? = { null },
) : DashboardRepository {
    override suspend fun loadDashboard(): DashboardLoadResult {
        return try {
            api.health()
            val accessToken = accessTokenProvider()?.trim().orEmpty()
            if (accessToken.isEmpty()) {
                DashboardLoadResult.Success(
                    activeLoad = null,
                    weatherSnapshot = null,
                    dryingPrediction = null,
                    sourceLabel = "Backend online",
                )
            } else {
                loadLaundryDashboard(accessToken)
            }
        } catch (exception: IOException) {
            DashboardLoadResult.Error("Backend is unreachable. Check connection and API URL.")
        } catch (exception: HttpException) {
            DashboardLoadResult.Error("Backend health check failed with status ${exception.code()}.")
        } catch (exception: Exception) {
            DashboardLoadResult.Error("Dashboard could not load backend data.")
        }
    }

    private suspend fun loadLaundryDashboard(accessToken: String): DashboardLoadResult {
        return when (val result = laundryDataSource.listLaundryLoads(accessToken)) {
            is RemoteDataResult.Success -> {
                val activeLoad = result.data.firstOrNull { it.status.isActive() }
                val weather = weatherDataSource.currentWeather(
                    accessToken = accessToken,
                    locationId = householdSettingsRepository.getWeatherLocation().id,
                )
                val prediction = activeLoad?.let { load ->
                    predictionDataSource?.calculateDryingPrediction(
                        accessToken = accessToken,
                        request = load.toPredictionRequest(),
                    )
                }
                val predictionData = (prediction as? RemoteDataResult.Success)?.data
                DashboardLoadResult.Success(
                    activeLoad = activeLoad,
                    weatherSnapshot = predictionData?.weatherSnapshot ?: (weather as? RemoteDataResult.Success)?.data,
                    dryingPrediction = predictionData,
                    sourceLabel = when {
                        predictionData != null -> "Backend prediction"
                        weather is RemoteDataResult.Success -> "Backend weather"
                        prediction is RemoteDataResult.Error -> "Backend data"
                        else -> "Backend data"
                    },
                )
            }

            is RemoteDataResult.Error -> DashboardLoadResult.Error(result.toDashboardMessage())
        }
    }

    private fun LaundryLoad.toPredictionRequest(): CreateRemoteDryingPrediction {
        return CreateRemoteDryingPrediction(
            laundryLoadId = id,
            clothingType = clothingType,
            washingProgram = washingProgram,
            dryingMethod = dryingLocation.toDryingMethod(),
            locationId = location.id,
            dryingLocation = dryingLocation,
            spinRpm = spinRpm,
            loadSize = loadSize,
        )
    }

    private fun DryingLocation.toDryingMethod(): DryingMethod {
        return when (this) {
            DryingLocation.INDOOR, DryingLocation.LAUNDRY_ROOM -> DryingMethod.INDOOR
            DryingLocation.BALCONY, DryingLocation.OUTDOOR_LINE, DryingLocation.PATIO -> DryingMethod.OUTDOOR
        }
    }
}

private fun LaundryLoadStatus.isActive(): Boolean {
    return this != LaundryLoadStatus.COMPLETED && this != LaundryLoadStatus.CANCELLED
}

private fun RemoteDataResult.Error.toDashboardMessage(): String {
    return when (type) {
        RemoteDataErrorType.AUTHENTICATION -> "Sign in is required to load laundry dashboard data."
        RemoteDataErrorType.NETWORK -> "Backend is unreachable. Check connection and API URL."
        RemoteDataErrorType.SERVER -> "Backend could not prepare dashboard data."
        RemoteDataErrorType.NOT_FOUND -> "No backend laundry data was found."
        RemoteDataErrorType.INVALID_RESPONSE -> "Backend returned dashboard data in an unexpected format."
        RemoteDataErrorType.UNKNOWN -> message
    }
}
