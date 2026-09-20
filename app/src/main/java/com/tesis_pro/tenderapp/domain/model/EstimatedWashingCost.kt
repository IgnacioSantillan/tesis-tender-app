package com.tesis_pro.tenderapp.domain.model

data class EstimatedWashingCost(
    val amount: Double?,
    val currency: String?,
    val level: EstimatedWashingCostLevel?,
    val confidence: EstimatedWashingCostConfidence?,
    val estimatedEnergyKwh: Double?,
    val estimatedWaterLiters: Double?,
)

enum class EstimatedWashingCostLevel {
    LOW,
    MEDIUM,
    HIGH,
    UNKNOWN;

    companion object {
        fun fromCode(value: String?): EstimatedWashingCostLevel? {
            return runCatching {
                enumValueOf<EstimatedWashingCostLevel>(value?.uppercase().orEmpty())
            }.getOrNull()
        }
    }
}

enum class EstimatedWashingCostConfidence {
    LOW,
    MEDIUM,
    HIGH;

    companion object {
        fun fromCode(value: String?): EstimatedWashingCostConfidence? {
            return runCatching {
                enumValueOf<EstimatedWashingCostConfidence>(value?.uppercase().orEmpty())
            }.getOrNull()
        }
    }
}
