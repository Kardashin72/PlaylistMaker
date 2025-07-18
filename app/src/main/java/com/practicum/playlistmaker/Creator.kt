package com.practicum.playlistmaker

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.TracksSearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.TracksSearchRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.api.TracksSearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksSearchHistoryRepository
import com.practicum.playlistmaker.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.domain.api.TracksSearchRepository
import com.practicum.playlistmaker.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksSearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksSearchInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksSearchRepository {
        return TracksSearchRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksSearchInteractor(): TracksSearchInteractor {
        return TracksSearchInteractorImpl(getTracksRepository())
    }

    fun provideTracksSearchHistoryInteractor(context: Context): TracksSearchHistoryInteractor {
        val sharedPreferences = context.getSharedPreferences(
            TracksSearchHistoryRepositoryImpl.HISTORY_PREFERENCES_KEY, Context.MODE_PRIVATE)
        val repository: TracksSearchHistoryRepository = TracksSearchHistoryRepositoryImpl(sharedPreferences, Gson())
        return TracksSearchHistoryInteractorImpl(repository)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val sharedPreferences = context.getSharedPreferences(
            SettingsRepositoryImpl.THEME_PREFERENCES, Context.MODE_PRIVATE)
        val repository: SettingsRepository = SettingsRepositoryImpl(sharedPreferences)
        return SettingsInteractorImpl(repository)
    }
}