package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.presentation.ui.SettingsActivity

class App : Application() {

    private var _darkTheme = false
    val darkTheme get() = _darkTheme


    override fun onCreate() {
        super.onCreate()

        //восстановление темы из shared_preferenes
        val theme_shared_prefs = getSharedPreferences(SettingsActivity.Companion.THEME_PREFERENCES, MODE_PRIVATE)

        val isDarkMode = theme_shared_prefs.getBoolean(SettingsActivity.Companion.SWITCHER_KEY, false)

        switchTheme(isDarkMode)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        _darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}