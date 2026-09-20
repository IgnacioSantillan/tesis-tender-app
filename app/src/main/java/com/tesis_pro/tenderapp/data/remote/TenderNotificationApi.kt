package com.tesis_pro.tenderapp.data.remote

import com.tesis_pro.tenderapp.data.remote.dto.DeviceRegistrationResponseDto
import com.tesis_pro.tenderapp.data.remote.dto.RegisterDeviceRequestDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TenderNotificationApi {
    @POST("notifications/register-device")
    suspend fun registerDevice(
        @Header("Authorization") authorization: String,
        @Body request: RegisterDeviceRequestDto,
    ): DeviceRegistrationResponseDto
}
