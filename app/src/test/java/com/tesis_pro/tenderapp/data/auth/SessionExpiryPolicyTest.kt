package com.tesis_pro.tenderapp.data.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionExpiryPolicyTest {
    @Test
    fun resolveExpiresAtEpochMillis_prefersExplicitExpiration() {
        val expiresAt = SessionExpiryPolicy.resolveExpiresAtEpochMillis(
            nowEpochMillis = 1_000L,
            expiresInSeconds = 3600,
            explicitExpiresAtEpochMillis = 9_000L,
        )

        assertEquals(9_000L, expiresAt)
    }

    @Test
    fun resolveExpiresAtEpochMillis_calculatesFromExpiresInWhenExplicitValueIsMissing() {
        val expiresAt = SessionExpiryPolicy.resolveExpiresAtEpochMillis(
            nowEpochMillis = 1_000L,
            expiresInSeconds = 3600,
            explicitExpiresAtEpochMillis = null,
        )

        assertEquals(3_601_000L, expiresAt)
    }

    @Test
    fun isExpired_usesGraceWindowBeforeTokenExpiration() {
        assertFalse(
            SessionExpiryPolicy.isExpired(
                nowEpochMillis = 1_000L,
                expiresAtEpochMillis = 120_000L,
            )
        )
        assertTrue(
            SessionExpiryPolicy.isExpired(
                nowEpochMillis = 60_000L,
                expiresAtEpochMillis = 120_000L,
            )
        )
    }

    @Test
    fun isExpired_keepsSessionWithoutExpirationUsable() {
        assertFalse(
            SessionExpiryPolicy.isExpired(
                nowEpochMillis = 10_000L,
                expiresAtEpochMillis = null,
            )
        )
    }
}
