package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.CreateDryingPredictionRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.CreateCompletionPlanRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.DryingPredictionResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.LaundryCompletionPlanResponseDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TenderPredictionApi {
    @POST("predictions/drying")
    suspend fun calculateDryingPrediction(
        @Header("Authorization") authorization: String,
        @Body request: CreateDryingPredictionRequestDto,
    ): DryingPredictionResponseDto

    @POST("predictions/completion-options")
    suspend fun calculateCompletionPlan(
        @Header("Authorization") authorization: String,
        @Body request: CreateCompletionPlanRequestDto,
    ): LaundryCompletionPlanResponseDto
}
