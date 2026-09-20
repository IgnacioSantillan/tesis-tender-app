package com.tesis_pro.tenderapp.data.auth

import com.tesis_pro.tenderapp.domain.model.AuthSession
import com.tesis_pro.tenderapp.domain.repository.AuthErrorType
import com.tesis_pro.tenderapp.domain.repository.AuthRepository
import com.tesis_pro.tenderapp.domain.repository.AuthResult
import com.tesis_pro.tenderapp.domain.repository.SessionTokenProvider

class SmokeAuthRepository(
    private val sessionTokenProvider: SessionTokenProvider = BuildConfigSessionTokenProvider(),
) : AuthRepository {
    override suspend fun signIn(
        email: String,
        password: String,
    ): AuthResult {
        val accessToken = sessionTokenProvider.getAccessToken()
        return if (accessToken == null) {
            AuthResult.Error(
                type = AuthErrorType.SERVICE_UNAVAILABLE,
                message = "Authentication service is pending; configure a smoke token to validate backend access.",
            )
        } else {
            AuthResult.Success(
                session = AuthSession(
                    email = email.trim(),
                    accessToken = accessToken,
                )
            )
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
    ): AuthResult {
        return AuthResult.RegistrationPending(email = email.trim())
    }

    override suspend fun requestPasswordRecovery(email: String): AuthResult {
        return AuthResult.PasswordRecoverySent(email = email.trim())
    }
}
