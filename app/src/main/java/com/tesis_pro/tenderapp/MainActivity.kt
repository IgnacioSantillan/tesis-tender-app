package com.tesis_pro.tenderapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.tesis_pro.tenderapp.data.notification.AndroidNotificationPermissionStateReader
import com.tesis_pro.tenderapp.data.notification.NotificationRuntimePermissionState
import com.tesis_pro.tenderapp.data.location.DeviceLocation
import com.tesis_pro.tenderapp.data.location.DeviceLocationResult
import com.tesis_pro.tenderapp.data.settings.AppAppearancePreferencesStore
import com.tesis_pro.tenderapp.data.settings.AppLanguagePreference
import com.tesis_pro.tenderapp.data.settings.AppThemePreference
import com.tesis_pro.tenderapp.ui.TenderApp
import com.tesis_pro.tenderapp.ui.theme.TenderAppTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    private var authCallbackUri by mutableStateOf<String?>(null)
    private var notificationPermissionState by mutableStateOf(
        NotificationRuntimePermissionState.NOT_REQUIRED,
    )
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        refreshNotificationPermissionState()
    }
    private var pendingLocationCallback: ((DeviceLocationResult) -> Unit)? = null
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val callback = pendingLocationCallback
        pendingLocationCallback = null
        callback?.invoke(
            if (granted) {
                readLastKnownDeviceLocation()
            } else {
                DeviceLocationResult.PermissionDenied
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authCallbackUri = intent?.dataString
        refreshNotificationPermissionState()
        enableEdgeToEdge()
        setContent {
            val appearanceStore = remember { AppAppearancePreferencesStore(this) }
            var appearancePreferences by remember {
                mutableStateOf(appearanceStore.getPreferences())
            }
            val darkTheme = appearancePreferences.theme.resolve(isSystemInDarkTheme())
            val localizedContext = LocalContext.current.localizedFor(appearancePreferences.language)
            CompositionLocalProvider(LocalContext provides localizedContext) {
                TenderAppTheme(darkTheme = darkTheme) {
                    TenderApp(
                        authCallbackUri = authCallbackUri,
                        notificationPermissionState = notificationPermissionState,
                        onRequestNotificationPermission = ::requestNotificationPermission,
                        onUseCurrentLocation = ::requestCurrentLocation,
                        appearancePreferences = appearancePreferences,
                        onLanguagePreferenceChanged = { language ->
                            appearancePreferences = appearanceStore.saveLanguage(language)
                        },
                        onThemePreferenceChanged = { theme ->
                            appearancePreferences = appearanceStore.saveTheme(theme)
                        },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshNotificationPermissionState()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        authCallbackUri = intent.dataString
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            refreshNotificationPermissionState()
        }
    }

    private fun refreshNotificationPermissionState() {
        notificationPermissionState = AndroidNotificationPermissionStateReader(this).read()
    }

    private fun requestCurrentLocation(onResult: (DeviceLocationResult) -> Unit) {
        if (hasLocationPermission()) {
            onResult(readLastKnownDeviceLocation())
        } else {
            pendingLocationCallback = onResult
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun readLastKnownDeviceLocation(): DeviceLocationResult {
        if (!hasLocationPermission()) return DeviceLocationResult.PermissionDenied
        val locationManager = getSystemService(LocationManager::class.java)
        val location = locationManager
            ?.getProviders(true)
            ?.mapNotNull { provider -> runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull() }
            ?.maxByOrNull(Location::getTime)
        return location?.let {
            DeviceLocationResult.Success(
                DeviceLocation(
                    latitude = it.latitude,
                    longitude = it.longitude,
                )
            )
        } ?: DeviceLocationResult.Unavailable
    }
}

private fun AppThemePreference.resolve(systemDarkTheme: Boolean): Boolean {
    return when (this) {
        AppThemePreference.SYSTEM -> systemDarkTheme
        AppThemePreference.LIGHT -> false
        AppThemePreference.DARK -> true
    }
}

private fun Context.localizedFor(language: AppLanguagePreference): Context {
    val locale = when (language) {
        AppLanguagePreference.SYSTEM -> return this
        AppLanguagePreference.SPANISH -> Locale("es")
        AppLanguagePreference.ENGLISH -> Locale("en")
    }
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)
    return createConfigurationContext(configuration)
}
