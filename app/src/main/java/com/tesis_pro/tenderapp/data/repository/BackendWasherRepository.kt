package com.tesis_pro.tenderapp.data.repository

import com.tesis_pro.tenderapp.data.remote.CreateRemoteWasher
import com.tesis_pro.tenderapp.data.remote.RemoteDataErrorType
import com.tesis_pro.tenderapp.data.remote.RemoteDataResult
import com.tesis_pro.tenderapp.data.remote.RemoteWasherDataSource
import com.tesis_pro.tenderapp.domain.model.Washer
import com.tesis_pro.tenderapp.domain.model.WasherType
import kotlinx.coroutines.flow.MutableStateFlow

class BackendWasherRepository(
    private val washerDataSource: RemoteWasherDataSource,
    private val accessTokenProvider: () -> String?,
) {
    private val washers = MutableStateFlow<List<Washer>>(emptyList())

    fun currentWashers(): List<Washer> = washers.value

    suspend fun refreshWashers(): WasherRefreshResult {
        val accessToken = accessTokenProvider()?.trim().orEmpty()
        if (accessToken.isEmpty()) {
            return WasherRefreshResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Sign in is required to load washers.",
            )
        }

        return when (val result = washerDataSource.listWashers(accessToken)) {
            is RemoteDataResult.Success -> {
                washers.value = result.data.sortedWith(
                    compareByDescending<Washer> { it.isPrimary }.thenBy { it.name },
                )
                WasherRefreshResult.Success(washers.value)
            }

            is RemoteDataResult.Error -> WasherRefreshResult.Error(
                type = result.type,
                message = result.message,
            )
        }
    }

    suspend fun createWasher(request: CreateRemoteWasher): WasherMutationResult {
        val accessToken = accessTokenProvider()?.trim().orEmpty()
        if (accessToken.isEmpty()) {
            return WasherMutationResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Sign in is required to create washers.",
            )
        }

        return when (val result = washerDataSource.createWasher(accessToken, request)) {
            is RemoteDataResult.Success -> {
                washers.value = (listOf(result.data) + washers.value)
                    .distinctBy { it.id }
                    .sortedWith(compareByDescending<Washer> { it.isPrimary }.thenBy { it.name })
                WasherMutationResult.Success(result.data)
            }

            is RemoteDataResult.Error -> WasherMutationResult.Error(
                type = result.type,
                message = result.message,
            )
        }
    }

    suspend fun updateWasher(
        washerId: String,
        request: CreateRemoteWasher,
    ): WasherMutationResult {
        val accessToken = accessTokenProvider()?.trim().orEmpty()
        if (accessToken.isEmpty()) {
            return WasherMutationResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Sign in is required to update washers.",
            )
        }

        return when (val result = washerDataSource.updateWasher(accessToken, washerId, request)) {
            is RemoteDataResult.Success -> {
                washers.value = (listOf(result.data) + washers.value.filterNot { it.id == washerId })
                    .distinctBy { it.id }
                    .sortedWith(compareByDescending<Washer> { it.isPrimary }.thenBy { it.name })
                WasherMutationResult.Success(result.data)
            }

            is RemoteDataResult.Error -> WasherMutationResult.Error(
                type = result.type,
                message = result.message,
            )
        }
    }

    suspend fun retireWasher(washerId: String): WasherMutationResult {
        val accessToken = accessTokenProvider()?.trim().orEmpty()
        if (accessToken.isEmpty()) {
            return WasherMutationResult.Error(
                type = RemoteDataErrorType.AUTHENTICATION,
                message = "Sign in is required to retire washers.",
            )
        }

        return when (val result = washerDataSource.retireWasher(accessToken, washerId)) {
            is RemoteDataResult.Success -> {
                val retiredWasher = washers.value.firstOrNull { it.id == washerId }
                washers.value = washers.value.filterNot { it.id == washerId }
                WasherMutationResult.Success(retiredWasher ?: retiredWasherPlaceholder(washerId))
            }

            is RemoteDataResult.Error -> WasherMutationResult.Error(
                type = result.type,
                message = result.message,
            )
        }
    }
}

sealed interface WasherRefreshResult {
    data class Success(val washers: List<Washer>) : WasherRefreshResult

    data class Error(
        val type: RemoteDataErrorType,
        val message: String,
    ) : WasherRefreshResult
}

private fun retiredWasherPlaceholder(washerId: String): Washer {
    return Washer(
        id = washerId,
        name = washerId,
        type = WasherType.OTHER,
        capacityKg = null,
        energyLabel = null,
        waterUsageLiters = null,
        isPrimary = false,
        defaultSpinRpm = null,
    )
}

sealed interface WasherMutationResult {
    data class Success(val washer: Washer) : WasherMutationResult

    data class Error(
        val type: RemoteDataErrorType,
        val message: String,
    ) : WasherMutationResult
}
