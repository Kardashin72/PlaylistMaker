package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {
    override fun isDarkTheme(): Boolean = sharedPreferences.getBoolean(SWITCHER_KEY, false)
    override fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(SWITCHER_KEY, enabled).apply()
    }

    companion object {
        const val THEME_PREFERENCES = "THEME_PREFERENCES"
        const val SWITCHER_KEY = "SWITCHER_KEY"
    }
}