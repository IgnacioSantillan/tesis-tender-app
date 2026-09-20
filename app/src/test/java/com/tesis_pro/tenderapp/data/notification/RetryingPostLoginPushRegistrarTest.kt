package com.tesis_pro.tenderapp.data.notification

import com.tesis_pro.tenderapp.domain.model.AuthSession
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RetryingPostLoginPushRegistrarTest {
    @Test
    fun registerAfterLogin_enqueuesRetryWhenDelegateFails() = runBlocking {
        val retryScheduler = FakePushRegistrationRetryScheduler()
        val registrar = RetryingPostLoginPushRegistrar(
            delegate = FakeRetryDelegatePushRegistrar(PushRegistrationResult.Failed("offline")),
            retryScheduler = retryScheduler,
        )

        val result = registrar.registerAfterLogin(sampleSession())

        assertEquals(PushRegistrationResult.Failed("offline"), result)
        assertEquals(1, retryScheduler.enqueueCount)
    }

    @Test
    fun registerAfterLogin_doesNotEnqueueRetryWhenDelegateSucceeds() = runBlocking {
        val retryScheduler = FakePushRegistrationRetryScheduler()
        val registrar = RetryingPostLoginPushRegistrar(
            delegate = FakeRetryDelegatePushRegistrar(PushRegistrationResult.Registered),
            retryScheduler = retryScheduler,
        )

        val result = registrar.registerAfterLogin(sampleSession())

        assertEquals(PushRegistrationResult.Registered, result)
        assertEquals(0, retryScheduler.enqueueCount)
    }
}

private class FakeRetryDelegatePushRegistrar(
    private val result: PushRegistrationResult,
) : PostLoginPushRegistrar {
    override suspend fun registerAfterLogin(session: AuthSession): PushRegistrationResult {
        return result
    }
}

private class FakePushRegistrationRetryScheduler : PushRegistrationRetryScheduler {
    var enqueueCount = 0

    override fun enqueueRetry() {
        enqueueCount += 1
    }
}

private fun sampleSession(): AuthSession {
    return AuthSession(
        email = "user@example.com",
        accessToken = "session-token",
        emailVerified = true,
    )
}
