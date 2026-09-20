package com.tesis_pro.tenderapp.data.notification

import com.tesis_pro.tenderapp.domain.model.AuthSession
import com.tesis_pro.tenderapp.domain.repository.AuthSessionStore
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StoredSessionPushRegistrationTest {
    @Test
    fun registerIfSessionExists_skipsWhenNoSessionIsStored() = runBlocking {
        val registrar = FakePostLoginPushRegistrar()
        val registration = StoredSessionPushRegistration(
            sessionStore = FakeAuthSessionStore(null),
            postLoginPushRegistrar = registrar,
        )

        val result = registration.registerIfSessionExists()

        assertNull(result)
        assertNull(registrar.lastSession)
    }

    @Test
    fun registerIfSessionExists_registersStoredSession() = runBlocking {
        val session = AuthSession(
            email = "user@example.com",
            accessToken = "token-1",
            emailVerified = true,
        )
        val registrar = FakePostLoginPushRegistrar()
        val registration = StoredSessionPushRegistration(
            sessionStore = FakeAuthSessionStore(session),
            postLoginPushRegistrar = registrar,
        )

        val result = registration.registerIfSessionExists()

        assertEquals(PushRegistrationResult.Registered, result)
        assertEquals(session, registrar.lastSession)
    }
}

private class FakeAuthSessionStore(
    private val session: AuthSession?,
) : AuthSessionStore {
    override fun getSession(): AuthSession? = session

    override fun getAccessToken(): String? = session?.accessToken

    override fun saveSession(session: AuthSession) = Unit

    override fun clearSession() = Unit
}

private class FakePostLoginPushRegistrar : PostLoginPushRegistrar {
    var lastSession: AuthSession? = null

    override suspend fun registerAfterLogin(session: AuthSession): PushRegistrationResult {
        lastSession = session
        return PushRegistrationResult.Registered
    }
}
