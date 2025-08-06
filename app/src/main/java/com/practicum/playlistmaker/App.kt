package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator

class App : Application() {

    private var _darkTheme = false
    val darkTheme get() = _darkTheme


    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        val settingsInteractor = Creator.provideSettingsInteractor()
        val isDarkMode = settingsInteractor.isDarkTheme()
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