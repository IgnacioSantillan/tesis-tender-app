package com.tesis_pro.tenderapp.data.remote.dto

data class SupabaseHealthResponseDto(
    val status: String,
    val projectHost: String,
    val checkedAt: String,
    val message: String?,
)
