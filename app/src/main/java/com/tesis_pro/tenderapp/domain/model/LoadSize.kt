package com.tesis_pro.tenderapp.domain.model

enum class LoadSize {
    SMALL,
    MEDIUM,
    LARGE;

    companion object {
        fun fromCode(value: String?): LoadSize? {
            return runCatching {
                enumValueOf<LoadSize>(value?.uppercase().orEmpty())
            }.getOrNull()
        }
    }
}
