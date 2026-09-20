package com.tesis_pro.tenderapp.domain.model

data class AuthSession(
    val email: String,
    val accessToken: String,
    val refreshToken: String? = null,
    val expiresInSeconds: Int? = null,
    val expiresAtEpochMillis: Long? = null,
    val emailVerified: Boolean = false,
)
