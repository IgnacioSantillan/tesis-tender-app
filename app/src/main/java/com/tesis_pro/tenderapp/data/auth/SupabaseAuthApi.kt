package com.tesis_pro.tenderapp.data.auth

import com.tesis_pro.tenderapp.data.auth.dto.SupabasePasswordSignInRequestDto
import com.tesis_pro.tenderapp.data.auth.dto.SupabasePasswordRecoveryRequestDto
import com.tesis_pro.tenderapp.data.auth.dto.SupabasePasswordSignUpRequestDto
import com.tesis_pro.tenderapp.data.auth.dto.SupabaseSessionResponseDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseAuthApi {
    @POST("auth/v1/token")
    suspend fun signInWithPassword(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("grant_type") grantType: String = "password",
        @Body request: SupabasePasswordSignInRequestDto,
    ): SupabaseSessionResponseDto

    @POST("auth/v1/signup")
    suspend fun signUpWithPassword(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("redirect_to") redirectTo: String?,
        @Body request: SupabasePasswordSignUpRequestDto,
    ): SupabaseSessionResponseDto

    @POST("auth/v1/recover")
    suspend fun requestPasswordRecovery(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("redirect_to") redirectTo: String?,
        @Body request: SupabasePasswordRecoveryRequestDto,
    )
}
