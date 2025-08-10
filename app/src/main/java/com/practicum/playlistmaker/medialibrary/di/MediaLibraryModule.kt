package com.practicum.playlistmaker.medialibrary.di

import com.practicum.playlistmaker.medialibrary.presentation.ui.MediaLibraryFragment
import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.MediaLibraryViewModel
import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {
    viewModel { MediaLibraryViewModel() }
    viewModel { PlaylistsViewModel() }
}