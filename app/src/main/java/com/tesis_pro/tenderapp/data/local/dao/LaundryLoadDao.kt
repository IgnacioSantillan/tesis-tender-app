package com.tesis_pro.tenderapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.tesis_pro.tenderapp.data.local.entity.LaundryLoadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaundryLoadDao {
    @Query("SELECT * FROM laundry_loads WHERE completedAtEpochMillis IS NULL LIMIT 1")
    fun observeActiveLoad(): Flow<LaundryLoadEntity?>

    @Query("SELECT * FROM laundry_loads ORDER BY createdAtEpochMillis DESC")
    fun observeHistory(): Flow<List<LaundryLoadEntity>>
}
