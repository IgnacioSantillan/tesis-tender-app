package com.tesis_pro.tenderapp.domain.repository

import com.tesis_pro.tenderapp.domain.model.Washer
import kotlinx.coroutines.flow.Flow

interface WasherRepository {
    fun observeWashers(): Flow<List<Washer>>

    fun observePrimaryWasher(): Flow<Washer?>

    suspend fun saveWasher(washer: Washer)
}
