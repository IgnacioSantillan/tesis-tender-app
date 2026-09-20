package com.tesis_pro.tenderapp.data.notification

import android.content.Context

class FirebasePushTokenStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    fun saveLatestToken(token: String) {
        preferences.edit()
            .putString(KEY_LATEST_TOKEN, token)
            .apply()
    }

    fun getLatestToken(): String? {
        return preferences.getString(KEY_LATEST_TOKEN, null)
            ?.takeIf { it.isNotBlank() }
    }

    companion object {
        private const val PREFERENCES_NAME = "tender_push_tokens"
        private const val KEY_LATEST_TOKEN = "latest_fcm_registration_token"
    }
}
