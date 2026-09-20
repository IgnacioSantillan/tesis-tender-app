package com.tesis_pro.tenderapp.domain.repository

import com.tesis_pro.tenderapp.domain.model.AuthSession

interface AuthRepository {
    suspend fun signIn(
        email: String,
        password: String,
    ): AuthResult

    suspend fun signUp(
        email: String,
        password: String,
    ): AuthResult

    suspend fun requestPasswordRecovery(
        email: String,
    ): AuthResult
}

sealed interface AuthResult {
    data class Success(
        val session: AuthSession,
    ) : AuthResult

    data class RegistrationPending(
        val email: String,
        val emailVerified: Boolean = false,
    ) : AuthResult

    data class PasswordRecoverySent(
        val email: String,
    ) : AuthResult

    data class Error(
        val type: AuthErrorType,
        val message: String,
    ) : AuthResult
}

enum class AuthErrorType {
    CONFIGURATION,
    INVALID_CREDENTIALS,
    SERVICE_UNAVAILABLE,
    UNKNOWN,
}
