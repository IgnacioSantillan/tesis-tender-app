package com.tesis_pro.tenderapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.tesis_pro.tenderapp.R
import com.tesis_pro.tenderapp.data.auth.BuildConfigSessionTokenProvider
import com.tesis_pro.tenderapp.data.auth.CompositeSessionTokenProvider
import com.tesis_pro.tenderapp.data.auth.SecureAuthSessionStore
import com.tesis_pro.tenderapp.data.notification.AndroidPostLoginPushRegistrar
import com.tesis_pro.tenderapp.data.notification.FirebasePushTokenProvider
import com.tesis_pro.tenderapp.data.notification.FirebasePushTokenStore
import com.tesis_pro.tenderapp.data.notification.RetryingPostLoginPushRegistrar
import com.tesis_pro.tenderapp.data.notification.SharedPreferencesNotificationPreferencesRepository
import com.tesis_pro.tenderapp.data.notification.WorkManagerPushRegistrationRetryScheduler
import com.tesis_pro.tenderapp.ui.components.MetricIcon
import com.tesis_pro.tenderapp.ui.components.MetricKind
import com.tesis_pro.tenderapp.ui.components.TonalPill
import com.tesis_pro.tenderapp.ui.theme.TenderTheme

@Composable
fun AuthScreen(
    authCallbackUri: String? = null,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val context = LocalContext.current.applicationContext
    val sessionStore = remember(context) {
        SecureAuthSessionStore(context)
    }
    val notificationPreferencesRepository = remember(context) {
        SharedPreferencesNotificationPreferencesRepository(context)
    }
    val viewModel = remember(sessionStore, notificationPreferencesRepository) {
        AuthViewModel(
            sessionTokenProvider = CompositeSessionTokenProvider(
                sessionStore,
                BuildConfigSessionTokenProvider(),
            ),
            authSessionStore = sessionStore,
            postLoginPushRegistrar = RetryingPostLoginPushRegistrar(
                delegate = AndroidPostLoginPushRegistrar(
                    tokenProvider = FirebasePushTokenProvider(
                        tokenStore = FirebasePushTokenStore(context),
                    ),
                    notificationOptInProvider = {
                        notificationPreferencesRepository.currentPreferences().notificationsEnabled
                    },
                ),
                retryScheduler = WorkManagerPushRegistrationRetryScheduler(context),
            ),
        )
    }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(authCallbackUri) {
        viewModel.onAuthCallback(authCallbackUri)
    }

    AuthScreenContent(
        state = state,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onModeChanged = viewModel::onModeChanged,
        onSubmit = viewModel::submit,
        onSignOut = viewModel::signOut,
        onContinue = onContinue,
        onBack = onBack,
    )
}

@Composable
fun AuthScreenContent(
    state: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onModeChanged: (AuthMode) -> Unit,
    onSubmit: () -> Unit,
    onSignOut: () -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AuthHeroCard(configured = state.smokeTokenConfigured)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AuthModeHeader(mode = state.mode)
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.auth_email)) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                if (state.mode != AuthMode.RECOVER_PASSWORD) {
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = onPasswordChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.auth_password)) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    )
                }
                Text(
                    text = state.signedInEmail?.let { stringResource(R.string.auth_signed_in_as, it) }
                        ?: localizedAuthMessage(state.message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = onSubmit,
                    enabled = state.canSubmit,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(authSubmitLabel(state.mode, state.isSubmitting))
                }
                if (state.signedInEmail != null) {
                    Button(
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSubmitting,
                    ) {
                        Text(stringResource(R.string.auth_continue))
                    }
                }
                AuthSecondaryActions(
                    mode = state.mode,
                    isSubmitting = state.isSubmitting,
                    hasSignedInUser = state.signedInEmail != null,
                    onBack = onBack,
                    onSignOut = onSignOut,
                    onModeChanged = onModeChanged,
                )
            }
        }
    }
}

@Composable
private fun AuthHeroCard(configured: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    MetricIcon(
                        kind = MetricKind.HUMIDITY,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp),
                    )
                }
                Text(
                    text = stringResource(R.string.auth_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(
                text = stringResource(R.string.auth_hero_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                text = stringResource(R.string.auth_hero_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
            )
            SessionStatusChip(configured = configured)
        }
    }
}

@Composable
private fun AuthModeHeader(mode: AuthMode) {
    val title = when (mode) {
        AuthMode.SIGN_IN -> stringResource(R.string.auth_sign_in_title)
        AuthMode.REGISTER -> stringResource(R.string.auth_register_title)
        AuthMode.RECOVER_PASSWORD -> stringResource(R.string.auth_recovery_title)
    }
    val description = when (mode) {
        AuthMode.SIGN_IN -> stringResource(R.string.auth_sign_in_description)
        AuthMode.REGISTER -> stringResource(R.string.auth_register_description)
        AuthMode.RECOVER_PASSWORD -> stringResource(R.string.auth_recovery_description)
    }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AuthSecondaryActions(
    mode: AuthMode,
    isSubmitting: Boolean,
    hasSignedInUser: Boolean,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onModeChanged: (AuthMode) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (hasSignedInUser) {
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
            ) {
                Text(stringResource(R.string.auth_sign_out))
            }
        }
        TextButton(
            onClick = {
                onModeChanged(
                    if (mode == AuthMode.SIGN_IN) {
                        AuthMode.REGISTER
                    } else {
                        AuthMode.SIGN_IN
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting,
        ) {
            Text(
                if (mode == AuthMode.SIGN_IN) {
                    stringResource(R.string.auth_create_account)
                } else {
                    stringResource(R.string.auth_use_existing_account)
                }
            )
        }
        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.auth_back))
        }
        if (mode == AuthMode.SIGN_IN) {
            TextButton(
                onClick = { onModeChanged(AuthMode.RECOVER_PASSWORD) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
            ) {
                Text(stringResource(R.string.auth_forgot_password))
            }
        }
    }
}

@Composable
private fun authSubmitLabel(
    mode: AuthMode,
    isSubmitting: Boolean,
): String {
    return if (isSubmitting) {
        stringResource(R.string.auth_checking)
    } else if (mode == AuthMode.SIGN_IN) {
        stringResource(R.string.auth_sign_in)
    } else if (mode == AuthMode.REGISTER) {
        stringResource(R.string.auth_register)
    } else {
        stringResource(R.string.auth_send_link)
    }
}

@Composable
private fun SessionStatusChip(configured: Boolean) {
    val statusColors = TenderTheme.statusColors
    val container = if (configured) statusColors.goodContainer else statusColors.warnContainer
    val content = if (configured) statusColors.onGoodContainer else statusColors.onWarnContainer
    TonalPill(
        text = if (configured) {
            stringResource(R.string.auth_token_configured)
        } else {
            stringResource(R.string.auth_no_active_session)
        },
        container = container,
        contentColor = content,
        showDot = true,
    )
}

@Composable
private fun localizedAuthMessage(message: AuthMessage): String = when (message) {
    AuthMessage.AccountCreated -> stringResource(R.string.auth_msg_account_created)
    is AuthMessage.AccountCreatedVerifyEmail -> stringResource(
        R.string.auth_msg_account_created_verify,
        message.email,
    )
    AuthMessage.AuthServicePending -> stringResource(R.string.auth_msg_service_pending)
    AuthMessage.CreateAccount -> stringResource(R.string.auth_msg_create_account)
    AuthMessage.EmailVerificationPending -> stringResource(R.string.auth_msg_email_pending)
    is AuthMessage.PasswordRecoverySent -> stringResource(
        R.string.auth_msg_recovery_sent,
        message.email,
    )
    is AuthMessage.ProviderError -> message.message
    AuthMessage.RecoveryPrompt -> stringResource(R.string.auth_msg_recovery_prompt)
    AuthMessage.SessionReady -> stringResource(R.string.auth_msg_session_ready)
    AuthMessage.SessionRequired -> stringResource(R.string.auth_msg_session_required)
    AuthMessage.SessionRestored -> stringResource(R.string.auth_msg_session_restored)
    AuthMessage.SignedOut -> stringResource(R.string.auth_msg_signed_out)
    AuthMessage.SmokeTokenConfigured -> stringResource(R.string.auth_msg_smoke_token_configured)
}
