package com.tesis_pro.tenderapp.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.tesis_pro.tenderapp.domain.model.AuthSession
import com.tesis_pro.tenderapp.domain.repository.AuthSessionStore

class SecureAuthSessionStore(
    context: Context,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) : AuthSessionStore {
    private val preferences: SharedPreferences = createPreferences(context.applicationContext)

    override fun getSession(): AuthSession? {
        val email = preferences.getString(KEY_EMAIL, null)?.takeIf { it.isNotBlank() } ?: return null
        val accessToken = preferences.getString(KEY_ACCESS_TOKEN, null)?.takeIf { it.isNotBlank() } ?: return null
        val expiresInSeconds = preferences.getInt(KEY_EXPIRES_IN, NO_EXPIRES_IN).takeIf {
            it != NO_EXPIRES_IN
        }
        if (!preferences.contains(KEY_EXPIRES_AT) && expiresInSeconds != null) {
            clearSession()
            return null
        }
        val expiresAtEpochMillis = preferences.getLong(KEY_EXPIRES_AT, NO_EXPIRES_AT).takeIf {
            it != NO_EXPIRES_AT
        }
        if (SessionExpiryPolicy.isExpired(currentTimeMillis(), expiresAtEpochMillis)) {
            clearSession()
            return null
        }
        return AuthSession(
            email = email,
            accessToken = accessToken,
            refreshToken = preferences.getString(KEY_REFRESH_TOKEN, null)?.takeIf { it.isNotBlank() },
            expiresInSeconds = expiresInSeconds,
            expiresAtEpochMillis = expiresAtEpochMillis,
            emailVerified = preferences.getBoolean(KEY_EMAIL_VERIFIED, false),
        )
    }

    override fun getAccessToken(): String? {
        return getSession()?.accessToken
    }

    override fun saveSession(session: AuthSession) {
        val expiresAtEpochMillis = SessionExpiryPolicy.resolveExpiresAtEpochMillis(
            nowEpochMillis = currentTimeMillis(),
            expiresInSeconds = session.expiresInSeconds,
            explicitExpiresAtEpochMillis = session.expiresAtEpochMillis,
        )
        preferences.edit()
            .putString(KEY_EMAIL, session.email)
            .putString(KEY_ACCESS_TOKEN, session.accessToken)
            .putString(KEY_REFRESH_TOKEN, session.refreshToken.orEmpty())
            .putInt(KEY_EXPIRES_IN, session.expiresInSeconds ?: NO_EXPIRES_IN)
            .putLong(KEY_EXPIRES_AT, expiresAtEpochMillis ?: NO_EXPIRES_AT)
            .putBoolean(KEY_EMAIL_VERIFIED, session.emailVerified)
            .apply()
    }

    override fun clearSession() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val FILE_NAME = "tenderapp_auth_session"
        const val KEY_EMAIL = "email"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_EXPIRES_IN = "expires_in"
        const val KEY_EXPIRES_AT = "expires_at"
        const val KEY_EMAIL_VERIFIED = "email_verified"
        const val NO_EXPIRES_IN = -1
        const val NO_EXPIRES_AT = -1L

        fun createPreferences(context: Context): SharedPreferences {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            return EncryptedSharedPreferences.create(
                context,
                FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }
    }
}
