package com.tesis_pro.tenderapp.domain.repository

import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import kotlinx.coroutines.flow.Flow

interface LaundryRepository {
    fun observeActiveLoad(): Flow<LaundryLoad?>

    fun observeHistory(): Flow<List<LaundryLoad>>
}
