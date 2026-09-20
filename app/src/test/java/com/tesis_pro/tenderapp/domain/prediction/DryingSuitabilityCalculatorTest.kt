package com.tesis_pro.tenderapp.domain.prediction

import com.tesis_pro.tenderapp.domain.model.ClothingType
import com.tesis_pro.tenderapp.domain.model.DryingMethod
import com.tesis_pro.tenderapp.domain.model.DryingVerdict
import com.tesis_pro.tenderapp.domain.model.LaundryLoad
import com.tesis_pro.tenderapp.domain.model.LaundryLoadStatus
import com.tesis_pro.tenderapp.domain.model.WashingProgram
import com.tesis_pro.tenderapp.domain.model.WeatherCondition
import com.tesis_pro.tenderapp.domain.model.WeatherLocation
import com.tesis_pro.tenderapp.domain.model.WeatherSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DryingSuitabilityCalculatorTest {
    private val calculator = DryingSuitabilityCalculator()

    @Test
    fun calculate_returnsGoodVerdictForFavorableOutdoorWeather() {
        val prediction = calculator.calculate(
            load = load(clothingType = ClothingType.LIGHT_CLOTHES),
            weather = weather(
                rainProbabilityPercent = 5,
                humidityPercent = 35,
                temperatureCelsius = 27.0,
                windSpeedKph = 12.0,
                cloudCoverPercent = 10,
            ),
            method = DryingMethod.OUTDOOR,
        )

        assertEquals(DryingVerdict.GOOD, prediction.verdict)
        assertTrue(prediction.suitabilityScore >= 70)
        assertTrue(prediction.reason.isNotBlank())
    }

    @Test
    fun calculate_returnsBadVerdictWhenOutdoorRainRiskIsHigh() {
        val prediction = calculator.calculate(
            load = load(clothingType = ClothingType.MIXED),
            weather = weather(
                rainProbabilityPercent = 80,
                humidityPercent = 40,
                temperatureCelsius = 26.0,
                windSpeedKph = 14.0,
                cloudCoverPercent = 20,
            ),
            method = DryingMethod.OUTDOOR,
        )

        assertEquals(DryingVerdict.BAD, prediction.verdict)
        assertTrue(prediction.reason.contains("Rain risk"))
    }

    @Test
    fun calculate_keepsIndoorDryingPossibleWhenRainRiskIsHigh() {
        val prediction = calculator.calculate(
            load = load(clothingType = ClothingType.MIXED),
            weather = weather(
                rainProbabilityPercent = 80,
                humidityPercent = 55,
                temperatureCelsius = 22.0,
                windSpeedKph = 8.0,
                cloudCoverPercent = 70,
            ),
            method = DryingMethod.INDOOR,
        )

        assertTrue(prediction.verdict != DryingVerdict.BAD)
        assertEquals(DryingMethod.INDOOR, prediction.method)
    }

    private fun load(clothingType: ClothingType): LaundryLoad {
        return LaundryLoad(
            id = "load-1",
            washerId = "washer-1",
            clothingType = clothingType,
            washingProgram = WashingProgram.NORMAL,
            status = LaundryLoadStatus.PLANNED,
            location = location(),
            createdAtEpochMillis = 1_000L,
            startedAtEpochMillis = null,
            completedAtEpochMillis = null,
            prediction = null,
        )
    }

    private fun weather(
        rainProbabilityPercent: Int,
        humidityPercent: Int,
        temperatureCelsius: Double,
        windSpeedKph: Double,
        cloudCoverPercent: Int,
    ): WeatherSnapshot {
        return WeatherSnapshot(
            capturedAtEpochMillis = 1_000L,
            forecastForEpochMillis = 2_000L,
            location = location(),
            condition = WeatherCondition.CLEAR,
            temperatureCelsius = temperatureCelsius,
            humidityPercent = humidityPercent,
            windSpeedKph = windSpeedKph,
            rainProbabilityPercent = rainProbabilityPercent,
            cloudCoverPercent = cloudCoverPercent,
        )
    }

    private fun location(): WeatherLocation {
        return WeatherLocation(
            id = "home",
            label = "Home",
            latitude = null,
            longitude = null,
        )
    }
}
