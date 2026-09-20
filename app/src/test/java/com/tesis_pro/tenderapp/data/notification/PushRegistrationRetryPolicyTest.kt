package com.tesis_pro.tenderapp.data.notification

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PushRegistrationRetryPolicyTest {
    @Test
    fun shouldEnqueueRetry_returnsTrueWhenRegistrationFails() {
        val result = PushRegistrationResult.Failed("Backend unavailable.")

        assertTrue(PushRegistrationRetryPolicy.shouldEnqueueRetry(result))
    }

    @Test
    fun shouldEnqueueRetry_returnsFalseWhenRegistrationSucceeds() {
        assertFalse(PushRegistrationRetryPolicy.shouldEnqueueRetry(PushRegistrationResult.Registered))
    }

    @Test
    fun shouldEnqueueRetry_returnsFalseWhenRegistrationIsSkipped() {
        val result = PushRegistrationResult.Skipped("No session.")

        assertFalse(PushRegistrationRetryPolicy.shouldEnqueueRetry(result))
    }
}
