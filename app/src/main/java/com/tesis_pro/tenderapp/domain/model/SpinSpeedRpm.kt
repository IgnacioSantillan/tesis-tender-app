package com.tesis_pro.tenderapp.domain.model

enum class SpinSpeedRpm(val rpm: Int) {
    RPM_600(600),
    RPM_800(800),
    RPM_1000(1000),
    RPM_1200(1200),
    RPM_1400(1400),
    RPM_1600(1600);

    companion object {
        fun fromRpm(value: Int?): SpinSpeedRpm? {
            return entries.firstOrNull { it.rpm == value }
        }
    }
}
