package com.tesis_pro.tenderapp.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import com.tesis_pro.tenderapp.data.auth.BuildConfigSessionTokenProvider
import com.tesis_pro.tenderapp.data.auth.SupabaseAuthCallbackParser
import com.tesis_pro.tenderapp.data.auth.SupabaseAuthRepository
import com.tesis_pro.tenderapp.data.notification.NoOpPostLoginPushRegistrar
import com.tesis_pro.tenderapp.data.notification.PostLoginPushRegistrar
import com.tesis_pro.tenderapp.domain.repository.AuthRepository
import com.tesis_pro.tenderapp.domain.repository.AuthResult
import com.tesis_pro.tenderapp.domain.repository.AuthSessionStore
import com.tesis_pro.tenderapp.domain.repository.SessionTokenProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val mode: AuthMode = AuthMode.SIGN_IN,
    val smokeTokenConfigured: Boolean = false,
    val isSubmitting: Boolean = false,
    val signedInEmail: String? = null,
    val message: AuthMessage = AuthMessage.SessionRequired,
) {
    val canSubmit: Boolean
        get() = email.isNotBlank() &&
            (mode == AuthMode.RECOVER_PASSWORD || password.isNotBlank()) &&
            !isSubmitting
}

sealed interface AuthMessage {
    data object SessionRequired : AuthMessage
    data object SessionRestored : AuthMessage
    data object CreateAccount : AuthMessage
    data object RecoveryPrompt : AuthMessage
    data object SessionReady : AuthMessage
    data object EmailVerificationPending : AuthMessage
    data object AccountCreated : AuthMessage
    data class AccountCreatedVerifyEmail(val email: String) : AuthMessage
    data class PasswordRecoverySent(val email: String) : AuthMessage
    data object SignedOut : AuthMessage
    data object SmokeTokenConfigured : AuthMessage
    data object AuthServicePending : AuthMessage
    data class ProviderError(val message: String) : AuthMessage
}

enum class AuthMode {
    SIGN_IN,
    REGISTER,
    RECOVER_PASSWORD,
}

class AuthViewModel(
    private val sessionTokenProvider: SessionTokenProvider = BuildConfigSessionTokenProvider(),
    private val authRepository: AuthRepository = SupabaseAuthRepository(),
    private val authSessionStore: AuthSessionStore? = null,
    private val postLoginPushRegistrar: PostLoginPushRegistrar = NoOpPostLoginPushRegistrar,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
) : ViewModel() {
    private val storedSession = authSessionStore?.getSession()
    private val _uiState = MutableStateFlow(
        AuthUiState(
            smokeTokenConfigured = sessionTokenProvider.getAccessToken() != null,
            signedInEmail = storedSession?.email,
            message = if (storedSession != null) {
                AuthMessage.SessionRestored
            } else {
                AuthMessage.SessionRequired
            },
        )
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { state -> state.copy(email = value) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { state -> state.copy(password = value) }
    }

    fun onModeChanged(mode: AuthMode) {
        _uiState.update { state ->
            state.copy(
                mode = mode,
                signedInEmail = null,
                message = if (mode == AuthMode.SIGN_IN) {
                    AuthMessage.SessionRequired
                } else if (mode == AuthMode.REGISTER) {
                    AuthMessage.CreateAccount
                } else {
                    AuthMessage.RecoveryPrompt
                },
            )
        }
    }

    fun submit() {
        val hasSmokeToken = sessionTokenProvider.getAccessToken() != null
        val currentState = _uiState.value
        if (!currentState.canSubmit) return

        _uiState.update { state -> state.copy(isSubmitting = true, smokeTokenConfigured = hasSmokeToken) }
        coroutineScope.launch {
            val result = when (currentState.mode) {
                AuthMode.SIGN_IN -> authRepository.signIn(
                    email = currentState.email,
                    password = currentState.password,
                )

                AuthMode.REGISTER -> authRepository.signUp(
                    email = currentState.email,
                    password = currentState.password,
                )

                AuthMode.RECOVER_PASSWORD -> authRepository.requestPasswordRecovery(
                    email = currentState.email,
                )
            }
            logAuthResult(result)
            _uiState.update { state ->
                when (result) {

                    is AuthResult.Success -> {
                        authSessionStore?.saveSession(result.session)
                        coroutineScope.launch {
                            postLoginPushRegistrar.registerAfterLogin(result.session)
                        }
                        state.copy(
                            isSubmitting = false,
                            smokeTokenConfigured = true,
                            signedInEmail = result.session.email,
                            message = if (result.session.emailVerified) {
                                AuthMessage.SessionReady
                            } else {
                                AuthMessage.EmailVerificationPending
                            },
                        )
                    }

                    is AuthResult.Error -> state.copy(
                        isSubmitting = false,
                        smokeTokenConfigured = hasSmokeToken,
                        signedInEmail = null,
                        message = AuthMessage.ProviderError(result.message),
                    )

                    is AuthResult.RegistrationPending -> state.copy(
                        isSubmitting = false,
                        smokeTokenConfigured = hasSmokeToken,
                        signedInEmail = null,
                        mode = AuthMode.SIGN_IN,
                        message = if (result.emailVerified) {
                            AuthMessage.AccountCreated
                        } else {
                            AuthMessage.AccountCreatedVerifyEmail(result.email)
                        },
                    )

                    is AuthResult.PasswordRecoverySent -> state.copy(
                        isSubmitting = false,
                        smokeTokenConfigured = hasSmokeToken,
                        signedInEmail = null,
                        mode = AuthMode.SIGN_IN,
                        message = AuthMessage.PasswordRecoverySent(result.email),
                    )

                }
            }
        }
    }

    fun signOut() {
        authSessionStore?.clearSession()
        val hasSmokeToken = sessionTokenProvider.getAccessToken() != null
        _uiState.update { state ->
            state.copy(
                smokeTokenConfigured = hasSmokeToken,
                signedInEmail = null,
                message = AuthMessage.SignedOut,
            )
        }
    }

    fun onAuthCallback(rawUri: String?) {
        val callback = SupabaseAuthCallbackParser.parse(rawUri) ?: return
        authSessionStore?.saveSession(callback.session)
        coroutineScope.launch {
            postLoginPushRegistrar.registerAfterLogin(callback.session)
        }
        _uiState.update { state ->
            state.copy(
                smokeTokenConfigured = true,
                signedInEmail = callback.session.email,
                mode = AuthMode.SIGN_IN,
                message = AuthMessage.SessionReady,
            )
        }
    }

    override fun onCleared() {
        coroutineScope.cancel()
        super.onCleared()
    }

    fun refreshSmokeTokenStatus() {
        val hasSmokeToken = sessionTokenProvider.getAccessToken() != null
        _uiState.update { state ->
            state.copy(
                smokeTokenConfigured = hasSmokeToken,
                message = if (hasSmokeToken) {
                    AuthMessage.SmokeTokenConfigured
                } else {
                    AuthMessage.AuthServicePending
                },
            )
        }
    }
}

private fun logAuthResult(result: AuthResult) {
    runCatching {
        Log.d("AuthViewModel", "submit: $result")
    }
}
