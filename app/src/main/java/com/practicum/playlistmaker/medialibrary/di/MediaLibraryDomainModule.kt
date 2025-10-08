package com.practicum.playlistmaker.medialibrary.di

import com.practicum.playlistmaker.medialibrary.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.medialibrary.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.impl.FavoritesInteractorImpl
import com.practicum.playlistmaker.medialibrary.domain.impl.PlaylistsInteractorImpl
import org.koin.dsl.module

val mediaLibraryDomainModule = module {
    factory<FavoritesInteractor> { FavoritesInteractorImpl(get()) }
    factory<PlaylistsInteractor> { PlaylistsInteractorImpl(get()) }
}