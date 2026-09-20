package com.tesis_pro.tenderapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tesis_pro.tenderapp.data.auth.SecureAuthSessionStore
import com.tesis_pro.tenderapp.data.notification.AndroidPostLoginPushRegistrar
import com.tesis_pro.tenderapp.data.notification.FirebasePushTokenProvider
import com.tesis_pro.tenderapp.data.notification.FirebasePushTokenStore
import com.tesis_pro.tenderapp.data.notification.NotificationRuntimePermissionState
import com.tesis_pro.tenderapp.data.notification.PushRegistrationResult
import com.tesis_pro.tenderapp.data.notification.RetryingPostLoginPushRegistrar
import com.tesis_pro.tenderapp.data.notification.SharedPreferencesNotificationPreferencesRepository
import com.tesis_pro.tenderapp.data.notification.StoredSessionPushRegistration
import com.tesis_pro.tenderapp.data.notification.WorkManagerPushRegistrationRetryScheduler
import com.tesis_pro.tenderapp.data.location.DeviceLocationResult
import com.tesis_pro.tenderapp.data.settings.AppAppearancePreferences
import com.tesis_pro.tenderapp.data.settings.AppLanguagePreference
import com.tesis_pro.tenderapp.data.settings.AppThemePreference
import com.tesis_pro.tenderapp.navigation.TenderDestination
import com.tesis_pro.tenderapp.ui.screens.AuthScreen
import com.tesis_pro.tenderapp.ui.screens.DashboardScreen
import com.tesis_pro.tenderapp.ui.screens.HistoryScreen
import com.tesis_pro.tenderapp.ui.screens.NewLoadScreen
import com.tesis_pro.tenderapp.ui.screens.SettingsScreen

private const val AuthRoute = "auth"

@Composable
fun TenderApp(
    authCallbackUri: String? = null,
    notificationPermissionState: NotificationRuntimePermissionState =
        NotificationRuntimePermissionState.NOT_REQUIRED,
    onRequestNotificationPermission: () -> Unit = {},
    onUseCurrentLocation: ((DeviceLocationResult) -> Unit) -> Unit = { callback ->
        callback(DeviceLocationResult.Unavailable)
    },
    appearancePreferences: AppAppearancePreferences = AppAppearancePreferences(),
    onLanguagePreferenceChanged: (AppLanguagePreference) -> Unit = {},
    onThemePreferenceChanged: (AppThemePreference) -> Unit = {},
) {
    val navController = rememberNavController()
    val destinations = TenderDestination.entries
    val context = LocalContext.current.applicationContext
    val sessionStore = remember(context) { SecureAuthSessionStore(context) }
    val notificationPreferencesRepository = remember(context) {
        SharedPreferencesNotificationPreferencesRepository(context)
    }
    val startupPushRegistration = remember(context, sessionStore) {
        StoredSessionPushRegistration(
            sessionStore = sessionStore,
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
    val startDestination = remember(sessionStore, authCallbackUri) {
        if (authCallbackUri != null || sessionStore.getSession() == null) {
            AuthRoute
        } else {
            TenderDestination.Dashboard.route
        }
    }
    LaunchedEffect(sessionStore.getAccessToken()) {
        val result = startupPushRegistration.registerIfSessionExists()
        if (result != null) {
            Log.d("TenderAppPush", "startup registration: ${result.toLogLabel()}")
        }
    }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showNavigation = currentDestination?.route?.let { route ->
        route != AuthRoute
    } ?: (startDestination != AuthRoute)

    if (showNavigation) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                destinations.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == destination.route
                    } == true

                    item(
                        icon = {
                            Icon(
                                painter = painterResource(destination.iconRes),
                                contentDescription = stringResource(destination.labelRes)
                            )
                        },
                        label = { Text(stringResource(destination.labelRes)) },
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(TenderDestination.Dashboard.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = destination != TenderDestination.Dashboard
                            }
                        }
                    )
                }
            }
        ) {
            TenderNavHost(
                navController = navController,
                startDestination = startDestination,
                authCallbackUri = authCallbackUri,
                notificationPermissionState = notificationPermissionState,
                onRequestNotificationPermission = onRequestNotificationPermission,
                onUseCurrentLocation = onUseCurrentLocation,
                appearancePreferences = appearancePreferences,
                onLanguagePreferenceChanged = onLanguagePreferenceChanged,
                onThemePreferenceChanged = onThemePreferenceChanged,
            )
        }
    } else {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                tenderGraph(
                    navController = navController,
                    authCallbackUri = authCallbackUri,
                    notificationPermissionState = notificationPermissionState,
                    onRequestNotificationPermission = onRequestNotificationPermission,
                    onUseCurrentLocation = onUseCurrentLocation,
                    appearancePreferences = appearancePreferences,
                    onLanguagePreferenceChanged = onLanguagePreferenceChanged,
                    onThemePreferenceChanged = onThemePreferenceChanged,
                )
            }
        }
    }
}

private fun PushRegistrationResult.toLogLabel(): String {
    return when (this) {
        PushRegistrationResult.Registered -> "registered"
        is PushRegistrationResult.Skipped -> "skipped"
        is PushRegistrationResult.Failed -> "failed"
    }
}

@Composable
private fun TenderNavHost(
    navController: NavHostController,
    startDestination: String,
    authCallbackUri: String?,
    notificationPermissionState: NotificationRuntimePermissionState,
    onRequestNotificationPermission: () -> Unit,
    onUseCurrentLocation: ((DeviceLocationResult) -> Unit) -> Unit,
    appearancePreferences: AppAppearancePreferences,
    onLanguagePreferenceChanged: (AppLanguagePreference) -> Unit,
    onThemePreferenceChanged: (AppThemePreference) -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            tenderGraph(
                navController = navController,
                authCallbackUri = authCallbackUri,
                notificationPermissionState = notificationPermissionState,
                onRequestNotificationPermission = onRequestNotificationPermission,
                onUseCurrentLocation = onUseCurrentLocation,
                appearancePreferences = appearancePreferences,
                onLanguagePreferenceChanged = onLanguagePreferenceChanged,
                onThemePreferenceChanged = onThemePreferenceChanged,
            )
        }
    }
}

private fun NavGraphBuilder.tenderGraph(
    navController: NavHostController,
    authCallbackUri: String? = null,
    notificationPermissionState: NotificationRuntimePermissionState =
        NotificationRuntimePermissionState.NOT_REQUIRED,
    onRequestNotificationPermission: () -> Unit = {},
    onUseCurrentLocation: ((DeviceLocationResult) -> Unit) -> Unit = { callback ->
        callback(DeviceLocationResult.Unavailable)
    },
    appearancePreferences: AppAppearancePreferences = AppAppearancePreferences(),
    onLanguagePreferenceChanged: (AppLanguagePreference) -> Unit = {},
    onThemePreferenceChanged: (AppThemePreference) -> Unit = {},
) {
    composable(TenderDestination.Dashboard.route) {
        DashboardScreen(
            onCreateNewLoad = {
                navController.navigate(TenderDestination.NewLoad.route) {
                    launchSingleTop = true
                }
            },
        )
    }
    composable(TenderDestination.NewLoad.route) {
        NewLoadScreen(
            onLoadCreated = {
                navController.navigate(TenderDestination.Dashboard.route) {
                    popUpTo(TenderDestination.Dashboard.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
        )
    }
    composable(TenderDestination.History.route) {
        HistoryScreen()
    }
    composable(TenderDestination.Settings.route) {
        SettingsScreen(
            notificationPermissionState = notificationPermissionState,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onUseCurrentLocation = onUseCurrentLocation,
            appearancePreferences = appearancePreferences,
            onLanguagePreferenceChanged = onLanguagePreferenceChanged,
            onThemePreferenceChanged = onThemePreferenceChanged,
            onOpenSession = { navController.navigate(AuthRoute) },
        )
    }
    composable(AuthRoute) {
        AuthScreen(
            authCallbackUri = authCallbackUri,
            onBack = { navController.popBackStack() },
            onContinue = {
                navController.navigate(TenderDestination.Dashboard.route) {
                    popUpTo(AuthRoute) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
        )
    }
}
