package com.tesis_pro.tenderapp.data.auth

object SessionExpiryPolicy {
    private const val EXPIRY_GRACE_MILLIS = 60_000L

    fun resolveExpiresAtEpochMillis(
        nowEpochMillis: Long,
        expiresInSeconds: Int?,
        explicitExpiresAtEpochMillis: Long?,
    ): Long? {
        explicitExpiresAtEpochMillis?.let { return it }
        val expiresIn = expiresInSeconds ?: return null
        if (expiresIn <= 0) return nowEpochMillis
        return nowEpochMillis + expiresIn * 1_000L
    }

    fun isExpired(
        nowEpochMillis: Long,
        expiresAtEpochMillis: Long?,
    ): Boolean {
        val expiresAt = expiresAtEpochMillis ?: return false
        return nowEpochMillis >= expiresAt - EXPIRY_GRACE_MILLIS
    }
}
