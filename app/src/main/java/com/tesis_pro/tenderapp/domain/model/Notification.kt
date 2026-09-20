package com.tesis_pro.tenderapp.domain.model

data class Notification(
    val id: String,
    val loadId: String,
    val message: String,
    val scheduledAtEpochMillis: Long,
    val deliveredAtEpochMillis: Long?,
)
