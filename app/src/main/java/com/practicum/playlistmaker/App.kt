package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.medialibrary.di.mediaLibraryDataModule
import com.practicum.playlistmaker.medialibrary.di.mediaLibraryDomainModule
import com.practicum.playlistmaker.medialibrary.di.mediaLibraryPresentationModule
import com.practicum.playlistmaker.player.di.playerModule
import com.practicum.playlistmaker.search.di.searchDataModule
import com.practicum.playlistmaker.search.di.searchDomainModule
import com.practicum.playlistmaker.search.di.searchViewModelModule
import com.practicum.playlistmaker.settings.di.settingsModule
import com.practicum.playlistmaker.share.di.shareModule
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    private val settingsInteractor: SettingsInteractor by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                searchDataModule,
                searchDomainModule,
                searchViewModelModule,
                settingsModule,
                shareModule,
                playerModule,
                mediaLibraryPresentationModule,
                mediaLibraryDataModule,
                mediaLibraryDomainModule
            )
        }
        val isDarkMode = settingsInteractor.isDarkTheme()
        switchTheme(isDarkMode)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}