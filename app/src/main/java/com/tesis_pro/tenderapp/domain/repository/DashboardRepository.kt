package com.tesis_pro.tenderapp.domain.repository

import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.DryingPrediction
import com.tesis_pro.tenderapp.domain.model.WeatherSnapshot

interface DashboardRepository {
    suspend fun loadDashboard(): DashboardLoadResult
}

sealed interface DashboardLoadResult {
    data class Success(
        val activeLoad: LaundryLoad?,
        val weatherSnapshot: WeatherSnapshot?,
        val dryingPrediction: DryingPrediction? = null,
        val sourceLabel: String,
    ) : DashboardLoadResult

    data class Error(
        val message: String,
    ) : DashboardLoadResult
}
