package com.practicum.playlistmaker.medialibrary.di

import androidx.room.Room
import com.practicum.playlistmaker.medialibrary.data.FavoritesRepositoryImpl
import com.practicum.playlistmaker.medialibrary.data.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.medialibrary.data.db.AppDatabase
import com.practicum.playlistmaker.medialibrary.domain.api.FavoritesRepository
import com.practicum.playlistmaker.medialibrary.domain.api.PlaylistsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mediaLibraryDataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "playlist_maker_db"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()
    }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
    single<PlaylistsRepository> { PlaylistsRepositoryImpl(get()) }
}