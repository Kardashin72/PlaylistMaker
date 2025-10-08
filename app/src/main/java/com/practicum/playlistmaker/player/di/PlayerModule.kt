package com.practicum.playlistmaker.player.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory { MediaPlayer() }
    factory<PlayerRepository> { PlayerRepositoryImpl(get()) }
    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }
    viewModel { (previewUrl: String) -> PlayerViewModel(previewUrl, get(), get(), get()) }
}
