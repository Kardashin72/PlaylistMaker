package com.practicum.playlistmaker.creator

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.search.data.TracksSearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.TracksSearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.search.domain.api.TracksSearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksSearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.search.domain.api.TracksSearchRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksSearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksSearchInteractorImpl
import com.practicum.playlistmaker.search.data.network.ItunesApiService
import com.practicum.playlistmaker.search.data.storage.PrefsStorageClient
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.share.data.ShareRepositoryImpl
import com.practicum.playlistmaker.share.domain.api.ShareFunctionsInteractor
import com.practicum.playlistmaker.share.domain.impl.ShareFunctionsInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideItunesApiService(): ItunesApiService {
        return provideRetrofit().create(ItunesApiService::class.java)
    }

    private fun getTracksRepository(): TracksSearchRepository {
        return TracksSearchRepositoryImpl(RetrofitNetworkClient(provideItunesApiService()))
    }

    fun provideTracksSearchInteractor(): TracksSearchInteractor {
        return TracksSearchInteractorImpl(getTracksRepository())
    }

    private fun getTracksSearchHistoryRepository(context: Context): TracksSearchHistoryRepository {
        return TracksSearchHistoryRepositoryImpl(PrefsStorageClient<ArrayList<Track>>(
            context,
            PrefsStorageClient.HISTORY_PREFERENCES_KEY,
            object : TypeToken<ArrayList<Track>>() {}.type)
        )
    }

    fun provideTracksSearchHistoryInteractor(context: Context): TracksSearchHistoryInteractor {
        return TracksSearchHistoryInteractorImpl(getTracksSearchHistoryRepository(context))
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val sharedPreferences = context.getSharedPreferences(
            SettingsRepositoryImpl.THEME_PREFERENCES, Context.MODE_PRIVATE)
        val repository: SettingsRepository = SettingsRepositoryImpl(sharedPreferences)
        return SettingsInteractorImpl(repository)
    }

    fun provideShareFunctionsInteractor(context: Context): ShareFunctionsInteractor {
        return ShareFunctionsInteractorImpl(ShareRepositoryImpl(context))
    }
}