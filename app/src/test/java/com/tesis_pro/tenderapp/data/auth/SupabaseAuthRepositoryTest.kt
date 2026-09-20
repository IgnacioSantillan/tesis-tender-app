package com.tesis_pro.tenderapp.data.auth

import com.tesis_pro.tenderapp.data.auth.dto.SupabaseSessionResponseDto
import com.tesis_pro.tenderapp.data.auth.dto.SupabaseUserDto
import com.tesis_pro.tenderapp.domain.repository.AuthErrorType
import com.tesis_pro.tenderapp.domain.repository.AuthResult
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class SupabaseAuthRepositoryTest {
    @Test
    fun signIn_returnsSessionWhenSupabaseRespondsWithTokens() = runBlocking {
        val api = FakeSupabaseAuthApi(
            result = SupabaseSessionResponseDto(
                accessToken = "access-token",
                refreshToken = "refresh-token",
                expiresIn = 3600,
                expiresAt = 1_783_735_546L,
                user = SupabaseUserDto(
                    id = "user-1",
                    email = "user@example.com",
                    emailConfirmedAt = "2026-07-07T12:00:00Z",
                    confirmedAt = null,
                ),
            )
        )
        val repository = SupabaseAuthRepository(
            config = configuredAuth(),
            apiFactory = { api },
        )

        val result = repository.signIn(
            email = " user@example.com ",
            password = "password",
        )

        assertTrue(result is AuthResult.Success)
        val success = result as AuthResult.Success
        assertEquals("user@example.com", success.session.email)
        assertEquals("access-token", success.session.accessToken)
        assertEquals("refresh-token", success.session.refreshToken)
        assertEquals(3600, success.session.expiresInSeconds)
        assertEquals(1_783_735_546_000L, success.session.expiresAtEpochMillis)
        assertTrue(success.session.emailVerified)
        assertEquals("sb_publishable_test", api.lastApiKey)
        assertEquals("Bearer sb_publishable_test", api.lastAuthorization)
        assertEquals("password", api.lastGrantType)
    }

    @Test
    fun signIn_returnsConfigurationErrorWhenPublicConfigIsMissing() = runBlocking {
        val repository = SupabaseAuthRepository(
            config = SupabaseAuthConfig(projectUrl = "", publishableKey = ""),
            apiFactory = { error("API should not be created without config") },
        )

        val result = repository.signIn(
            email = "user@example.com",
            password = "password",
        )

        assertTrue(result is AuthResult.Error)
        val error = result as AuthResult.Error
        assertEquals(AuthErrorType.CONFIGURATION, error.type)
    }

    @Test
    fun signIn_mapsUnauthorizedResponseToInvalidCredentials() = runBlocking {
        val api = FakeSupabaseAuthApi(
            throwable = HttpException(Response.error<SupabaseSessionResponseDto>(401, "".toResponseBody()))
        )
        val repository = SupabaseAuthRepository(
            config = configuredAuth(),
            apiFactory = { api },
        )

        val result = repository.signIn(
            email = "user@example.com",
            password = "wrong",
        )

        assertTrue(result is AuthResult.Error)
        val error = result as AuthResult.Error
        assertEquals(AuthErrorType.INVALID_CREDENTIALS, error.type)
    }

    @Test
    fun signUp_returnsPendingRegistrationWhenSupabaseRequiresEmailVerification() = runBlocking {
        val api = FakeSupabaseAuthApi(
            signUpResult = SupabaseSessionResponseDto(
                accessToken = null,
                refreshToken = null,
                expiresIn = null,
                user = SupabaseUserDto(
                    id = "user-2",
                    email = "new@example.com",
                    emailConfirmedAt = null,
                    confirmedAt = null,
                ),
            )
        )
        val repository = SupabaseAuthRepository(
            config = configuredAuth(),
            apiFactory = { api },
        )

        val result = repository.signUp(
            email = " new@example.com ",
            password = "password",
        )

        assertTrue(result is AuthResult.RegistrationPending)
        val pending = result as AuthResult.RegistrationPending
        assertEquals("new@example.com", pending.email)
        assertEquals(false, pending.emailVerified)
        assertEquals("sb_publishable_test", api.lastApiKey)
        assertEquals("Bearer sb_publishable_test", api.lastAuthorization)
        assertEquals("new@example.com", api.lastSignUpEmail)
        assertEquals("com.tesispro.tenderapp://auth-callback", api.lastRedirectTo)
    }

    @Test
    fun signUp_returnsSessionWhenSupabaseAutoConfirmsAccount() = runBlocking {
        val api = FakeSupabaseAuthApi(
            signUpResult = SupabaseSessionResponseDto(
                accessToken = "signup-access-token",
                refreshToken = "signup-refresh-token",
                expiresIn = 3600,
                user = SupabaseUserDto(
                    id = "user-3",
                    email = "confirmed@example.com",
                    emailConfirmedAt = "2026-07-07T12:00:00Z",
                    confirmedAt = null,
                ),
            )
        )
        val repository = SupabaseAuthRepository(
            config = configuredAuth(),
            apiFactory = { api },
        )

        val result = repository.signUp(
            email = "confirmed@example.com",
            password = "password",
        )

        assertTrue(result is AuthResult.Success)
        val success = result as AuthResult.Success
        assertEquals("confirmed@example.com", success.session.email)
        assertEquals("signup-access-token", success.session.accessToken)
        assertEquals("signup-refresh-token", success.session.refreshToken)
        assertTrue(success.session.emailVerified)
    }

    @Test
    fun requestPasswordRecovery_returnsSentResultWhenSupabaseAcceptsRequest() = runBlocking {
        val api = FakeSupabaseAuthApi()
        val repository = SupabaseAuthRepository(
            config = configuredAuth(),
            apiFactory = { api },
        )

        val result = repository.requestPasswordRecovery(email = " recover@example.com ")

        assertTrue(result is AuthResult.PasswordRecoverySent)
        val sent = result as AuthResult.PasswordRecoverySent
        assertEquals("recover@example.com", sent.email)
        assertEquals("sb_publishable_test", api.lastApiKey)
        assertEquals("Bearer sb_publishable_test", api.lastAuthorization)
        assertEquals("recover@example.com", api.lastRecoveryEmail)
        assertEquals("com.tesispro.tenderapp://auth-callback", api.lastRedirectTo)
    }
}

private fun configuredAuth(): SupabaseAuthConfig {
    return SupabaseAuthConfig(
        projectUrl = "https://example.supabase.co",
        publishableKey = "sb_publishable_test",
        authRedirectUrl = "com.tesispro.tenderapp://auth-callback",
    )
}

private class FakeSupabaseAuthApi(
    private val result: SupabaseSessionResponseDto? = null,
    private val signUpResult: SupabaseSessionResponseDto? = null,
    private val throwable: Throwable? = null,
) : SupabaseAuthApi {
    var lastApiKey: String? = null
    var lastAuthorization: String? = null
    var lastGrantType: String? = null
    var lastRedirectTo: String? = null
    var lastSignUpEmail: String? = null
    var lastRecoveryEmail: String? = null

    override suspend fun signInWithPassword(
        apiKey: String,
        authorization: String,
        grantType: String,
        request: com.tesis_pro.tenderapp.data.auth.dto.SupabasePasswordSignInRequestDto,
    ): SupabaseSessionResponseDto {
        lastApiKey = apiKey
        lastAuthorization = authorization
        lastGrantType = grantType
        throwable?.let { throw it }
        return requireNotNull(result)
    }

    override suspend fun signUpWithPassword(
        apiKey: String,
        authorization: String,
        redirectTo: String?,
        request: com.tesis_pro.tenderapp.data.auth.dto.SupabasePasswordSignUpRequestDto,
    ): SupabaseSessionResponseDto {
        lastApiKey = apiKey
        lastAuthorization = authorization
        lastRedirectTo = redirectTo
        lastSignUpEmail = request.email
        throwable?.let { throw it }
        return requireNotNull(signUpResult)
    }

    override suspend fun requestPasswordRecovery(
        apiKey: String,
        authorization: String,
        redirectTo: String?,
        request: com.tesis_pro.tenderapp.data.auth.dto.SupabasePasswordRecoveryRequestDto,
    ) {
        lastApiKey = apiKey
        lastAuthorization = authorization
        lastRedirectTo = redirectTo
        lastRecoveryEmail = request.email
        throwable?.let { throw it }
    }
}
