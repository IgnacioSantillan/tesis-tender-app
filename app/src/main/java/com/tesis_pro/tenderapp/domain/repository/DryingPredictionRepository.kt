package com.tesis_pro.tenderapp.domain.repository

import com.tesis_pro.tenderapp.domain.model.DryingMethod
import com.tesis_pro.tenderapp.domain.model.DryingPrediction
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.WeatherSnapshot

interface DryingPredictionRepository {
    fun calculateDryingPrediction(
        load: LaundryLoad,
        weather: WeatherSnapshot,
        method: DryingMethod = DryingMethod.OUTDOOR,
    ): DryingPrediction
}
