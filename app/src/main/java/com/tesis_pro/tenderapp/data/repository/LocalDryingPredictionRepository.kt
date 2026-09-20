package com.tesis_pro.tenderapp.data.repository

import com.tesis_pro.tenderapp.domain.model.DryingMethod
import com.tesis_pro.tenderapp.domain.model.DryingPrediction
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.WeatherSnapshot
import com.tesis_pro.tenderapp.domain.prediction.DryingSuitabilityCalculator
import com.tesis_pro.tenderapp.domain.repository.DryingPredictionRepository

class LocalDryingPredictionRepository(
    private val calculator: DryingSuitabilityCalculator = DryingSuitabilityCalculator(),
) : DryingPredictionRepository {
    override fun calculateDryingPrediction(
        load: LaundryLoad,
        weather: WeatherSnapshot,
        method: DryingMethod,
    ): DryingPrediction {
        return calculator.calculate(
            load = load,
            weather = weather,
            method = method,
        )
    }
}
