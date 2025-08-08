package com.practicum.playlistmaker.settings.di

import android.content.Context
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

val settingsModule = module {
    single {
        androidContext().getSharedPreferences(
            SettingsRepositoryImpl.THEME_PREFERENCES, Context.MODE_PRIVATE
        )
    }

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<SettingsInteractor> { SettingsInteractorImpl(get()) }

    viewModel { SettingsViewModel(get(), get()) }
}