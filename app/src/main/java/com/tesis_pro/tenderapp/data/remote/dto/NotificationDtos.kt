package com.tesis_pro.tenderapp.data.remote.dto

data class RegisterDeviceRequestDto(
    val deviceToken: String,
    val platform: String,
    val pushProvider: String,
    val notificationOptIn: Boolean,
    val appVersion: String?,
)

data class DeviceRegistrationResponseDto(
    val id: String,
    val platform: String,
    val pushProvider: String,
    val status: String,
    val registeredAt: String,
    val appVersion: String?,
)
