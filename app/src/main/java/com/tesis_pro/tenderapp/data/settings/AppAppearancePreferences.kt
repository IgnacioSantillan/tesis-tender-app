package com.tesis_pro.tenderapp.data.settings

import android.content.Context

data class AppAppearancePreferences(
    val language: AppLanguagePreference = AppLanguagePreference.SYSTEM,
    val theme: AppThemePreference = AppThemePreference.SYSTEM,
)

enum class AppLanguagePreference {
    SYSTEM,
    SPANISH,
    ENGLISH,
}

enum class AppThemePreference {
    SYSTEM,
    LIGHT,
    DARK,
}

class AppAppearancePreferencesStore(
    context: Context,
) {
    private val preferences = context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun getPreferences(): AppAppearancePreferences {
        return AppAppearancePreferences(
            language = preferences.getString(KEY_LANGUAGE, null).toLanguagePreference(),
            theme = preferences.getString(KEY_THEME, null).toThemePreference(),
        )
    }

    fun saveLanguage(language: AppLanguagePreference): AppAppearancePreferences {
        preferences.edit().putString(KEY_LANGUAGE, language.name).apply()
        return getPreferences()
    }

    fun saveTheme(theme: AppThemePreference): AppAppearancePreferences {
        preferences.edit().putString(KEY_THEME, theme.name).apply()
        return getPreferences()
    }

    private fun String?.toLanguagePreference(): AppLanguagePreference {
        return enumValues<AppLanguagePreference>().firstOrNull { it.name == this }
            ?: AppLanguagePreference.SYSTEM
    }

    private fun String?.toThemePreference(): AppThemePreference {
        return enumValues<AppThemePreference>().firstOrNull { it.name == this }
            ?: AppThemePreference.SYSTEM
    }

    private companion object {
        const val FILE_NAME = "tenderapp_appearance_preferences"
        const val KEY_LANGUAGE = "language"
        const val KEY_THEME = "theme"
    }
}
