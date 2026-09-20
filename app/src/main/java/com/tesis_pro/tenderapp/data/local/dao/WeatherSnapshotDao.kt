package com.tesis_pro.tenderapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tesis_pro.tenderapp.data.local.entity.WeatherSnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherSnapshotDao {
    @Query(
        """
        SELECT * FROM weather_snapshots
        WHERE locationId = :locationId
        ORDER BY forecastForEpochMillis DESC
        LIMIT 1
        """
    )
    fun observeLatestSnapshot(locationId: String): Flow<WeatherSnapshotEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSnapshot(snapshot: WeatherSnapshotEntity)
}
