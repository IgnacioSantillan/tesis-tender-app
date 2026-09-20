package com.tesis_pro.tenderapp.data.auth

import com.tesis_pro.tenderapp.data.auth.dto.SupabasePasswordSignInRequestDto
import com.tesis_pro.tenderapp.data.auth.dto.SupabasePasswordRecoveryRequestDto
import com.tesis_pro.tenderapp.data.auth.dto.SupabasePasswordSignUpRequestDto
import com.tesis_pro.tenderapp.domain.model.AuthSession
import com.tesis_pro.tenderapp.domain.repository.AuthErrorType
import com.tesis_pro.tenderapp.domain.repository.AuthRepository
import com.tesis_pro.tenderapp.domain.repository.AuthResult
import java.io.IOException
import retrofit2.HttpException

class SupabaseAuthRepository(
    private val config: SupabaseAuthConfig = SupabaseAuthConfig.fromBuildConfig(),
    private val apiFactory: (SupabaseAuthConfig) -> SupabaseAuthApi = SupabaseAuthApiClient::create,
) : AuthRepository {
    override suspend fun signIn(
        email: String,
        password: String,
    ): AuthResult {
        if (!config.isConfigured) {
            return AuthResult.Error(
                type = AuthErrorType.CONFIGURATION,
                message = "Supabase Auth public configuration is missing.",
            )
        }

        return try {
            val response = apiFactory(config).signInWithPassword(
                apiKey = config.publishableKey,
                authorization = "Bearer ${config.publishableKey}",
                request = SupabasePasswordSignInRequestDto(
                    email = email.trim(),
                    password = password,
                ),
            )

            val responseEmail = response.user?.email ?: email.trim()
            val accessToken = response.accessToken
                ?: return AuthResult.Error(
                    type = AuthErrorType.UNKNOWN,
                    message = "Supabase did not return an access token.",
                )
            AuthResult.Success(
                session = AuthSession(
                    email = responseEmail,
                    accessToken = accessToken,
                    refreshToken = response.refreshToken,
                    expiresInSeconds = response.expiresIn,
                    expiresAtEpochMillis = response.expiresAt?.let { it * 1_000L },
                    emailVerified = response.user?.isEmailVerified == true,
                )
            )
        } catch (exception: HttpException) {
            AuthResult.Error(
                type = if (exception.code() == 400 || exception.code() == 401) {
                    AuthErrorType.INVALID_CREDENTIALS
                } else {
                    AuthErrorType.SERVICE_UNAVAILABLE
                },
                message = "Unable to sign in with the provided credentials.",
            )
        } catch (exception: IOException) {
            AuthResult.Error(
                type = AuthErrorType.SERVICE_UNAVAILABLE,
                message = "Authentication service is unreachable.",
            )
        } catch (exception: Exception) {
            AuthResult.Error(
                type = AuthErrorType.UNKNOWN,
                message = "Authentication failed unexpectedly.",
            )
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
    ): AuthResult {
        if (!config.isConfigured) {
            return AuthResult.Error(
                type = AuthErrorType.CONFIGURATION,
                message = "Supabase Auth public configuration is missing.",
            )
        }

        val normalizedEmail = email.trim()
        return try {
            val response = apiFactory(config).signUpWithPassword(
                apiKey = config.publishableKey,
                authorization = "Bearer ${config.publishableKey}",
                redirectTo = config.authRedirectUrl.ifBlank { null },
                request = SupabasePasswordSignUpRequestDto(
                    email = normalizedEmail,
                    password = password,
                ),
            )
            val responseEmail = response.user?.email ?: normalizedEmail
            val accessToken = response.accessToken
            if (accessToken.isNullOrBlank()) {
                AuthResult.RegistrationPending(
                    email = responseEmail,
                    emailVerified = response.user?.isEmailVerified == true,
                )
            } else {
                AuthResult.Success(
                    session = AuthSession(
                        email = responseEmail,
                        accessToken = accessToken,
                        refreshToken = response.refreshToken,
                        expiresInSeconds = response.expiresIn,
                        expiresAtEpochMillis = response.expiresAt?.let { it * 1_000L },
                        emailVerified = response.user?.isEmailVerified == true,
                    )
                )
            }
        } catch (exception: HttpException) {
            AuthResult.Error(
                type = if (exception.code() == 400 || exception.code() == 401 || exception.code() == 422) {
                    AuthErrorType.INVALID_CREDENTIALS
                } else {
                    AuthErrorType.SERVICE_UNAVAILABLE
                },
                message = "Unable to register with the provided credentials.",
            )
        } catch (exception: IOException) {
            AuthResult.Error(
                type = AuthErrorType.SERVICE_UNAVAILABLE,
                message = "Authentication service is unreachable.",
            )
        } catch (exception: Exception) {
            AuthResult.Error(
                type = AuthErrorType.UNKNOWN,
                message = "Registration failed unexpectedly.",
            )
        }
    }

    override suspend fun requestPasswordRecovery(
        email: String,
    ): AuthResult {
        if (!config.isConfigured) {
            return AuthResult.Error(
                type = AuthErrorType.CONFIGURATION,
                message = "Supabase Auth public configuration is missing.",
            )
        }

        val normalizedEmail = email.trim()
        return try {
            apiFactory(config).requestPasswordRecovery(
                apiKey = config.publishableKey,
                authorization = "Bearer ${config.publishableKey}",
                redirectTo = config.authRedirectUrl.ifBlank { null },
                request = SupabasePasswordRecoveryRequestDto(email = normalizedEmail),
            )
            AuthResult.PasswordRecoverySent(email = normalizedEmail)
        } catch (exception: HttpException) {
            AuthResult.Error(
                type = if (exception.code() == 400 || exception.code() == 401 || exception.code() == 422) {
                    AuthErrorType.INVALID_CREDENTIALS
                } else {
                    AuthErrorType.SERVICE_UNAVAILABLE
                },
                message = "Unable to send the password recovery email.",
            )
        } catch (exception: IOException) {
            AuthResult.Error(
                type = AuthErrorType.SERVICE_UNAVAILABLE,
                message = "Authentication service is unreachable.",
            )
        } catch (exception: Exception) {
            AuthResult.Error(
                type = AuthErrorType.UNKNOWN,
                message = "Password recovery failed unexpectedly.",
            )
        }
    }
}
