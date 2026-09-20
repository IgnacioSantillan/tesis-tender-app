package com.tesis_pro.tenderapp.data.auth.dto

import com.google.gson.annotations.SerializedName

data class SupabasePasswordSignInRequestDto(
    val email: String,
    val password: String,
)

data class SupabasePasswordSignUpRequestDto(
    val email: String,
    val password: String,
)

data class SupabasePasswordRecoveryRequestDto(
    val email: String,
)

data class SupabaseSessionResponseDto(
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    @SerializedName("expires_in")
    val expiresIn: Int?,
    @SerializedName("expires_at")
    val expiresAt: Long? = null,
    val user: SupabaseUserDto?,
)

data class SupabaseUserDto(
    val id: String?,
    val email: String?,
    @SerializedName("email_confirmed_at")
    val emailConfirmedAt: String?,
    @SerializedName("confirmed_at")
    val confirmedAt: String?,
) {
    val isEmailVerified: Boolean
        get() = emailConfirmedAt != null || confirmedAt != null
}
