package com.tesis_pro.tenderapp.data.repository

import com.tesis_pro.tenderapp.data.local.dao.LaundryLoadDao
import com.tesis_pro.tenderapp.data.local.entity.LaundryLoadEntity
import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.domain.repository.LaundryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalLaundryRepository(
    private val laundryLoadDao: LaundryLoadDao,
) : LaundryRepository {
    override fun observeActiveLoad(): Flow<LaundryLoad?> {
        return laundryLoadDao.observeActiveLoad().map { entity ->
            entity?.toDomain()
        }
    }

    override fun observeHistory(): Flow<List<LaundryLoad>> {
        return laundryLoadDao.observeHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}

private fun LaundryLoadEntity.toDomain(): LaundryLoad {
    val status = if (completedAtEpochMillis == null) {
        LaundryLoadStatus.DRYING
    } else {
        LaundryLoadStatus.COMPLETED
    }

    return LaundryLoad(
        id = id,
        washerId = washerId,
        clothingType = clothingType.toEnumOrDefault(ClothingType.MIXED),
        washingProgram = washingProgram.toEnumOrDefault(WashingProgram.NORMAL),
        status = status,
        location = LocalHouseholdSettingsRepository.DefaultWeatherLocation,
        createdAtEpochMillis = createdAtEpochMillis,
        startedAtEpochMillis = null,
        completedAtEpochMillis = completedAtEpochMillis,
        prediction = null,
    )
}

private inline fun <reified T : Enum<T>> String.toEnumOrDefault(default: T): T {
    return enumValues<T>().firstOrNull { it.name == this } ?: default
}
