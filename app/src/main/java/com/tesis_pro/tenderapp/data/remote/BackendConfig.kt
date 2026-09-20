package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.BuildConfig

object BackendConfig {
    val baseUrl: String = normalizeBaseUrl(BuildConfig.BACKEND_BASE_URL)

    fun normalizeBaseUrl(value: String): String {
        val trimmed = value.trim()
        require(trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            "Backend base URL must use http or https."
        }

        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }
}
