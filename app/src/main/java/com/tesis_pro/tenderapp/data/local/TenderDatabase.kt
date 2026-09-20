package com.tesis_pro.tenderapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tesis_pro.tenderapp.data.local.dao.LaundryLoadDao
import com.tesis_pro.tenderapp.data.local.dao.WasherDao
import com.tesis_pro.tenderapp.data.local.dao.WeatherSnapshotDao
import com.tesis_pro.tenderapp.data.local.entity.LaundryLoadEntity
import com.tesis_pro.tenderapp.data.local.entity.WasherEntity
import com.tesis_pro.tenderapp.data.local.entity.WeatherSnapshotEntity

@Database(
    entities = [
        LaundryLoadEntity::class,
        WasherEntity::class,
        WeatherSnapshotEntity::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class TenderDatabase : RoomDatabase() {
    abstract fun laundryLoadDao(): LaundryLoadDao

    abstract fun washerDao(): WasherDao

    abstract fun weatherSnapshotDao(): WeatherSnapshotDao
}
