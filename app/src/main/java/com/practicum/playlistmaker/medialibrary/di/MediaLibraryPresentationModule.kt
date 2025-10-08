package com.practicum.playlistmaker.medialibrary.di

import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.CreatePlaylistViewModel
import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.FavouriteTracksViewModel
import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryPresentationModule = module {
    viewModel { FavouriteTracksViewModel(get()) }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { CreatePlaylistViewModel(get()) }
}