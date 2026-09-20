package com.tesis_pro.tenderapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tesis_pro.tenderapp.data.local.entity.WasherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WasherDao {
    @Query("SELECT * FROM washers ORDER BY isPrimary DESC, name ASC")
    fun observeWashers(): Flow<List<WasherEntity>>

    @Query("SELECT * FROM washers WHERE isPrimary = 1 LIMIT 1")
    fun observePrimaryWasher(): Flow<WasherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWasher(washer: WasherEntity)
}
