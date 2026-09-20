package com.tesis_pro.tenderapp.data.auth

import com.tesis_pro.tenderapp.BuildConfig

data class SupabaseAuthConfig(
    val projectUrl: String,
    val publishableKey: String,
    val authRedirectUrl: String = "",
) {
    val isConfigured: Boolean
        get() = projectUrl.isNotBlank() && publishableKey.isNotBlank()

    companion object {
        fun fromBuildConfig(): SupabaseAuthConfig {
            return SupabaseAuthConfig(
                projectUrl = normalizeProjectUrl(BuildConfig.SUPABASE_URL),
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY.trim(),
                authRedirectUrl = normalizeOptionalRedirectUrl(BuildConfig.SUPABASE_AUTH_REDIRECT_URL),
            )
        }

        fun normalizeProjectUrl(value: String): String {
            val trimmed = value.trim().trimEnd('/')
            if (trimmed.isEmpty()) return ""

            require(trimmed.startsWith("https://")) {
                "Supabase project URL must use https."
            }

            return trimmed
        }

        fun normalizeOptionalRedirectUrl(value: String): String {
            val trimmed = value.trim().trimEnd('/')
            if (trimmed.isEmpty()) return ""
            require(trimmed.contains("://")) {
                "Supabase auth redirect URL must include a URI scheme."
            }
            return trimmed
        }
    }
}
