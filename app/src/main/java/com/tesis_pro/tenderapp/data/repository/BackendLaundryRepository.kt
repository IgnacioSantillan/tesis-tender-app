package com.tesis_pro.tenderapp.data.repository

import com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType
import com.tesis_pro.tenderapp.data.remote.RemoteDataResult
import com.tesis_pro.tenderapp.data.remote.CreateRemoteLaundryLoad
import com.tesis_pro.tenderapp.data.remote.RemoteLaundryDataSource
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.repository.LaundryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

interface LaundryStatusUpdater {
    suspend fun updateLaundryLoadStatus(
        loadId: String,
        status: LaundryLoadStatus,
    ): LaundryMutationResult
}

interface LaundryHistoryRepository : LaundryStatusUpdater {
    suspend fun refreshHistory(): LaundryRefreshResult

    fun currentHistory(): List<LaundryLoad>
}

class BackendLaundryRepository(
    private val laundryDataSource: RemoteLaundryDataSource,
    private val accessTokenProvider: () -> String?,
) : LaundryRepository, LaundryStatusUpdater, LaundryHistoryRepository {
    private val loads = MutableStateFlow<List<LaundryLoad>>(emptyList())

    override fun observeActiveLoad(): Flow<LaundryLoad?> {
        return loads.map { currentLoads ->
            currentLoads.firstOrNull { it.status.isActive() }
        }
    }

    override fun observeHistory(): Flow<List<LaundryLoad>> = loads

    override fun currentHistory(): List<LaundryLoad> = loads.value

    override suspend fun refreshHistory(): LaundryRefreshResult {
        val accessToken = accessTokenProvider()?.trim().orEmpty()
        if (accessToken.isEmpty()) {
            return LaundryRefreshResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Sign in is required to load laundry history.",
            )
        }

        return when (val result = laundryDataSource.listLaundryLoads(accessToken)) {
            is RemoteDataResult.Success -> {
                loads.value = result.data.sortedByDescending { it.createdAtEpochMillis }
                LaundryRefreshResult.Success
            }

            is RemoteDataResult.Error -> LaundryRefreshResult.Error(
                type = result.type,
                message = result.message,
            )
        }
    }

    suspend fun createLaundryLoad(request: CreateRemoteLaundryLoad): LaundryMutationResult {
        val accessToken = accessTokenProvider()?.trim().orEmpty()
        if (accessToken.isEmpty()) {
            return LaundryMutationResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Sign in is required to create a laundry load.",
            )
        }

        return when (val result = laundryDataSource.createLaundryLoad(accessToken, request)) {
            is RemoteDataResult.Success -> {
                loads.value = (listOf(result.data) + loads.value)
                    .distinctBy { it.id }
                    .sortedByDescending { it.createdAtEpochMillis }
                LaundryMutationResult.Success(result.data)
            }

            is RemoteDataResult.Error -> LaundryMutationResult.Error(
                type = result.type,
                message = result.message,
            )
        }
    }

    override suspend fun updateLaundryLoadStatus(
        loadId: String,
        status: LaundryLoadStatus,
    ): LaundryMutationResult {
        val accessToken = accessTokenProvider()?.trim().orEmpty()
        if (accessToken.isEmpty()) {
            return LaundryMutationResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Sign in is required to update laundry loads.",
            )
        }

        return when (val result = laundryDataSource.updateLaundryLoadStatus(accessToken, loadId, status)) {
            is RemoteDataResult.Success -> {
                loads.value = (listOf(result.data) + loads.value)
                    .distinctBy { it.id }
                    .sortedByDescending { it.createdAtEpochMillis }
                LaundryMutationResult.Success(result.data)
            }

            is RemoteDataResult.Error -> LaundryMutationResult.Error(
                type = result.type,
                message = result.message,
            )
        }
    }
}

sealed interface LaundryRefreshResult {
    data object Success : LaundryRefreshResult

    data class Error(
        val type: RemoteDataErrorType,
        val message: String,
    ) : LaundryRefreshResult
}

sealed interface LaundryMutationResult {
    data class Success(val load: LaundryLoad) : LaundryMutationResult

    data class Error(
        val type: RemoteDataErrorType,
        val message: String,
    ) : LaundryMutationResult
}

private fun com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus.isActive(): Boolean {
    return this != com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus.COMPLETED &&
        this != com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus.CANCELLED
}
