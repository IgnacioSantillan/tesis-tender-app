package com.tesis_pro.tenderapp.ui.screens

import com.tesis_pro.tenderapp.domain.model.AuthSession
import com.tesis_pro.tenderapp.data.notification.PostLoginPushRegistrar
import com.tesis_pro.tenderapp.data.notification.PushRegistrationResult
import com.tesis_pro.tenderapp.domain.repository.AuthErrorType
import com.tesis_pro.tenderapp.domain.repository.AuthRepository
import com.tesis_pro.tenderapp.domain.repository.AuthResult
import com.tesis_pro.tenderapp.domain.repository.AuthSessionStore
import com.tesis_pro.tenderapp.domain.repository.SessionTokenProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthViewModelTest {
    @Test
    fun uiState_marksSubmitAvailableOnlyWhenCredentialsArePresent() {
        val viewModel = AuthViewModel(
            sessionTokenProvider = FakeSessionTokenProvider(null),
            authRepository = FakeAuthRepository(AuthResult.Error(AuthErrorType.SERVICE_UNAVAILABLE, "Unavailable")),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        assertFalse(viewModel.uiState.value.canSubmit)

        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("password")

        assertTrue(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun submit_reportsConfiguredSmokeTokenWhenAvailable() {
        val sessionStore = FakeAuthSessionStore()
        val pushRegistrar = FakePostLoginPushRegistrar()
        val viewModel = AuthViewModel(
            sessionTokenProvider = FakeSessionTokenProvider("token-1"),
            authRepository = FakeAuthRepository(
                AuthResult.Success(
                    AuthSession(
                        email = "user@example.com",
                        accessToken = "token-1",
                        emailVerified = true,
                    )
                )
            ),
            authSessionStore = sessionStore,
            postLoginPushRegistrar = pushRegistrar,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("password")

        viewModel.submit()

        val state = viewModel.uiState.value
        assertTrue(state.smokeTokenConfigured)
        assertEquals("user@example.com", state.signedInEmail)
        assertEquals(AuthMessage.SessionReady, state.message)
        assertEquals("token-1", sessionStore.getAccessToken())
        assertEquals("token-1", pushRegistrar.lastSession?.accessToken)
    }

    @Test
    fun init_restoresStoredSessionState() {
        val sessionStore = FakeAuthSessionStore(
            AuthSession(
                email = "stored@example.com",
                accessToken = "stored-token",
                emailVerified = true,
            )
        )
        val viewModel = AuthViewModel(
            sessionTokenProvider = sessionStore,
            authRepository = FakeAuthRepository(),
            authSessionStore = sessionStore,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        val state = viewModel.uiState.value
        assertTrue(state.smokeTokenConfigured)
        assertEquals("stored@example.com", state.signedInEmail)
        assertEquals(AuthMessage.SessionRestored, state.message)
    }

    @Test
    fun signOut_clearsStoredSession() {
        val sessionStore = FakeAuthSessionStore(
            AuthSession(
                email = "stored@example.com",
                accessToken = "stored-token",
                emailVerified = true,
            )
        )
        val viewModel = AuthViewModel(
            sessionTokenProvider = sessionStore,
            authRepository = FakeAuthRepository(),
            authSessionStore = sessionStore,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        viewModel.signOut()

        val state = viewModel.uiState.value
        assertEquals(null, sessionStore.getSession())
        assertEquals(null, state.signedInEmail)
        assertEquals(AuthMessage.SignedOut, state.message)
    }

    @Test
    fun submit_reportsMissingTokenWhenUnavailable() {
        val viewModel = AuthViewModel(
            sessionTokenProvider = FakeSessionTokenProvider(null),
            authRepository = FakeAuthRepository(
                AuthResult.Error(
                    type = AuthErrorType.SERVICE_UNAVAILABLE,
                    message = "Authentication service is pending; configure a smoke token to validate backend access.",
                )
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("password")

        viewModel.submit()

        val state = viewModel.uiState.value
        assertFalse(state.smokeTokenConfigured)
        assertEquals(null, state.signedInEmail)
        assertEquals(
            AuthMessage.ProviderError(
                "Authentication service is pending; configure a smoke token to validate backend access.",
            ),
            state.message,
        )
    }

    @Test
    fun submit_reportsRegistrationPendingAndReturnsToSignInMode() {
        val viewModel = AuthViewModel(
            sessionTokenProvider = FakeSessionTokenProvider(null),
            authRepository = FakeAuthRepository(
                signUpResult = AuthResult.RegistrationPending(
                    email = "new@example.com",
                    emailVerified = false,
                )
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        viewModel.onModeChanged(AuthMode.REGISTER)
        viewModel.onEmailChanged("new@example.com")
        viewModel.onPasswordChanged("password")

        viewModel.submit()

        val state = viewModel.uiState.value
        assertEquals(AuthMode.SIGN_IN, state.mode)
        assertEquals(null, state.signedInEmail)
        assertEquals(
            AuthMessage.AccountCreatedVerifyEmail("new@example.com"),
            state.message,
        )
    }

    @Test
    fun submit_reportsPasswordRecoverySentAndReturnsToSignInMode() {
        val viewModel = AuthViewModel(
            sessionTokenProvider = FakeSessionTokenProvider(null),
            authRepository = FakeAuthRepository(
                recoveryResult = AuthResult.PasswordRecoverySent(email = "recover@example.com")
            ),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        viewModel.onModeChanged(AuthMode.RECOVER_PASSWORD)
        viewModel.onEmailChanged("recover@example.com")

        viewModel.submit()

        val state = viewModel.uiState.value
        assertEquals(AuthMode.SIGN_IN, state.mode)
        assertEquals(null, state.signedInEmail)
        assertEquals(AuthMessage.PasswordRecoverySent("recover@example.com"), state.message)
    }

    @Test
    fun onAuthCallback_storesSessionAndMarksSessionReady() {
        val sessionStore = FakeAuthSessionStore()
        val pushRegistrar = FakePostLoginPushRegistrar()
        val viewModel = AuthViewModel(
            sessionTokenProvider = sessionStore,
            authRepository = FakeAuthRepository(),
            authSessionStore = sessionStore,
            postLoginPushRegistrar = pushRegistrar,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
        )

        viewModel.onAuthCallback(
            "com.tesispro.tenderapp://auth-callback?access_token=token-1&email=user%40example.com"
        )

        val state = viewModel.uiState.value
        assertTrue(state.smokeTokenConfigured)
        assertEquals("user@example.com", state.signedInEmail)
        assertEquals(AuthMessage.SessionReady, state.message)
        assertEquals("token-1", sessionStore.getAccessToken())
        assertEquals("token-1", pushRegistrar.lastSession?.accessToken)
    }
}

private class FakeSessionTokenProvider(
    private val token: String?,
) : SessionTokenProvider {
    override fun getAccessToken(): String? = token
}

private class FakeAuthSessionStore(
    private var session: AuthSession? = null,
) : AuthSessionStore {
    override fun getSession(): AuthSession? = session

    override fun getAccessToken(): String? = session?.accessToken

    override fun saveSession(session: AuthSession) {
        this.session = session
    }

    override fun clearSession() {
        session = null
    }
}

private class FakePostLoginPushRegistrar : PostLoginPushRegistrar {
    var lastSession: AuthSession? = null

    override suspend fun registerAfterLogin(session: AuthSession): PushRegistrationResult {
        lastSession = session
        return PushRegistrationResult.Registered
    }
}

private class FakeAuthRepository(
    private val result: AuthResult = AuthResult.Error(AuthErrorType.SERVICE_UNAVAILABLE, "Unavailable"),
    private val signUpResult: AuthResult = result,
    private val recoveryResult: AuthResult = result,
) : AuthRepository {
    override suspend fun signIn(
        email: String,
        password: String,
    ): AuthResult = result

    override suspend fun signUp(
        email: String,
        password: String,
    ): AuthResult = signUpResult

    override suspend fun requestPasswordRecovery(email: String): AuthResult = recoveryResult
}
