package com.tesis_pro.tenderapp.domain.prediction

import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingMethod
import com.tesis_pro.tenderapp.domain.model.DryingPrediction
import com.tesis_pro.tenderapp.domain.model.DryingVerdict
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.WeatherSnapshot

class DryingSuitabilityCalculator(
    private val config: DryingSuitabilityConfig = DryingSuitabilityConfig(),
) {
    fun calculate(
        load: LaundryLoad,
        weather: WeatherSnapshot,
        method: DryingMethod = DryingMethod.OUTDOOR,
    ): DryingPrediction {
        val score = calculateScore(weather = weather, method = method)
        val verdict = selectVerdict(score = score, weather = weather, method = method)
        val dryingMinutes = estimateDryingMinutes(
            clothingType = load.clothingType,
            weather = weather,
            method = method,
            verdict = verdict,
        )
        val recommendedHangAt = maxOf(load.createdAtEpochMillis, weather.forecastForEpochMillis)

        return DryingPrediction(
            verdict = verdict,
            method = method,
            estimatedDryingMinutes = dryingMinutes,
            recommendedHangAtEpochMillis = recommendedHangAt,
            estimatedPickupAtEpochMillis = recommendedHangAt + dryingMinutes.toLong() * MILLIS_PER_MINUTE,
            suitabilityScore = score,
            reason = buildReason(verdict = verdict, weather = weather, method = method),
            weatherSnapshot = weather,
        )
    }

    private fun calculateScore(
        weather: WeatherSnapshot,
        method: DryingMethod,
    ): Int {
        val rainPenalty = if (method == DryingMethod.OUTDOOR) {
            weather.rainProbabilityPercent
        } else {
            weather.rainProbabilityPercent / 3
        }
        val humidityPenalty = (weather.humidityPercent - config.idealHumidityPercent)
            .coerceAtLeast(0) / 2
        val cloudPenalty = weather.cloudCoverPercent / 5
        val coldPenalty = ((config.idealTemperatureCelsius - weather.temperatureCelsius) * 2)
            .toInt()
            .coerceAtLeast(0)
        val windBonus = weather.windSpeedKph
            .coerceAtMost(config.maxUsefulWindKph)
            .toInt()

        return (config.baseScore - rainPenalty - humidityPenalty - cloudPenalty - coldPenalty + windBonus)
            .coerceIn(config.minimumScore, config.maximumScore)
    }

    private fun selectVerdict(
        score: Int,
        weather: WeatherSnapshot,
        method: DryingMethod,
    ): DryingVerdict {
        if (method == DryingMethod.OUTDOOR &&
            weather.rainProbabilityPercent >= config.badRainProbabilityPercent
        ) {
            return DryingVerdict.BAD
        }

        return when {
            score >= config.goodScoreThreshold -> DryingVerdict.GOOD
            score >= config.cautionScoreThreshold -> DryingVerdict.CAUTION
            else -> DryingVerdict.BAD
        }
    }

    private fun estimateDryingMinutes(
        clothingType: ClothingType,
        weather: WeatherSnapshot,
        method: DryingMethod,
        verdict: DryingVerdict,
    ): Int {
        val baseMinutes = when (clothingType) {
            ClothingType.LIGHT_CLOTHES -> 120
            ClothingType.MIXED -> 180
            ClothingType.DELICATES -> 150
            ClothingType.HEAVY_CLOTHES -> 260
            ClothingType.BEDDING -> 300
        }
        val methodMultiplier = if (method == DryingMethod.INDOOR) 1.35 else 1.0
        val humidityMultiplier = 1.0 + weather.humidityPercent.coerceAtLeast(0) / 200.0
        val verdictMultiplier = when (verdict) {
            DryingVerdict.GOOD -> 0.9
            DryingVerdict.CAUTION -> 1.15
            DryingVerdict.BAD -> 1.35
        }

        return (baseMinutes * methodMultiplier * humidityMultiplier * verdictMultiplier)
            .toInt()
            .coerceAtLeast(config.minimumDryingMinutes)
    }

    private fun buildReason(
        verdict: DryingVerdict,
        weather: WeatherSnapshot,
        method: DryingMethod,
    ): String {
        if (method == DryingMethod.OUTDOOR &&
            weather.rainProbabilityPercent >= config.badRainProbabilityPercent
        ) {
            return "Rain risk is high, so outdoor drying is not recommended."
        }

        return when (verdict) {
            DryingVerdict.GOOD -> "Weather is favorable for drying: low rain risk and useful wind."
            DryingVerdict.CAUTION -> "Drying is possible, but humidity or clouds may slow it down."
            DryingVerdict.BAD -> "Conditions are weak for drying because humidity, rain or cold are high."
        }
    }

    private companion object {
        const val MILLIS_PER_MINUTE = 60_000L
    }
}

data class DryingSuitabilityConfig(
    val baseScore: Int = 85,
    val minimumScore: Int = 0,
    val maximumScore: Int = 100,
    val idealHumidityPercent: Int = 45,
    val idealTemperatureCelsius: Double = 20.0,
    val maxUsefulWindKph: Double = 20.0,
    val goodScoreThreshold: Int = 70,
    val cautionScoreThreshold: Int = 40,
    val badRainProbabilityPercent: Int = 70,
    val minimumDryingMinutes: Int = 60,
)
