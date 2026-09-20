package com.tesis_pro.tenderapp.data.auth

import com.tesis_pro.tenderapp.domain.repository.AuthErrorType
import com.tesis_pro.tenderapp.domain.repository.AuthResult
import com.tesis_pro.tenderapp.domain.repository.SessionTokenProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SmokeAuthRepositoryTest {
    @Test
    fun signIn_returnsSessionWhenSmokeTokenExists() = runBlocking {
        val repository = SmokeAuthRepository(
            sessionTokenProvider = FakeSessionTokenProvider("token-1")
        )

        val result = repository.signIn(
            email = " user@example.com ",
            password = "password",
        )

        assertTrue(result is AuthResult.Success)
        val success = result as AuthResult.Success
        assertEquals("user@example.com", success.session.email)
        assertEquals("token-1", success.session.accessToken)
    }

    @Test
    fun signIn_returnsServiceUnavailableWhenSmokeTokenIsMissing() = runBlocking {
        val repository = SmokeAuthRepository(
            sessionTokenProvider = FakeSessionTokenProvider(null)
        )

        val result = repository.signIn(
            email = "user@example.com",
            password = "password",
        )

        assertTrue(result is AuthResult.Error)
        val error = result as AuthResult.Error
        assertEquals(AuthErrorType.SERVICE_UNAVAILABLE, error.type)
    }
}

private class FakeSessionTokenProvider(
    private val token: String?,
) : SessionTokenProvider {
    override fun getAccessToken(): String? = token
}
