package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.HealthResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.CreateLaundryLoadRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.LaundryLoadResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.SupabaseHealthResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.UpdateLaundryLoadStatusRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.SaveUserLocationRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.SaveWasherRequestDto
import com.tesis_pro.tenderapp.data.remote.dto.UserLocationResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.WeatherSnapshotResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.WasherResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TenderBackendApi {
    @GET("health")
    suspend fun health(): HealthResponseDto

    @GET("health/supabase")
    suspend fun supabaseHealth(): SupabaseHealthResponseDto

    @GET("weather/current")
    suspend fun currentWeather(
        @Header("Authorization") authorization: String,
        @Query("locationId") locationId: String,
    ): WeatherSnapshotResponseDto

    @GET("laundry-loads")
    suspend fun listLaundryLoads(
        @Header("Authorization") authorization: String,
    ): List<LaundryLoadResponseDto>

    @GET("washers")
    suspend fun listWashers(
        @Header("Authorization") authorization: String,
    ): List<WasherResponseDto>

    @POST("washers")
    suspend fun createWasher(
        @Header("Authorization") authorization: String,
        @Body request: SaveWasherRequestDto,
    ): WasherResponseDto

    @PUT("washers/{id}")
    suspend fun updateWasher(
        @Header("Authorization") authorization: String,
        @Path("id") washerId: String,
        @Body request: SaveWasherRequestDto,
    ): WasherResponseDto

    @DELETE("washers/{id}")
    suspend fun retireWasher(
        @Header("Authorization") authorization: String,
        @Path("id") washerId: String,
    )

    @POST("laundry-loads")
    suspend fun createLaundryLoad(
        @Header("Authorization") authorization: String,
        @Body request: CreateLaundryLoadRequestDto,
    ): LaundryLoadResponseDto

    @PATCH("laundry-loads/{id}/status")
    suspend fun updateLaundryLoadStatus(
        @Header("Authorization") authorization: String,
        @Path("id") loadId: String,
        @Body request: UpdateLaundryLoadStatusRequestDto,
    ): LaundryLoadResponseDto

    @PUT("users/me/location")
    suspend fun saveUserLocation(
        @Header("Authorization") authorization: String,
        @Body request: SaveUserLocationRequestDto,
    ): UserLocationResponseDto
}
