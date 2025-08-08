package com.practicum.playlistmaker.player.di

import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    single<PlayerRepository> { PlayerRepositoryImpl() }
    single<PlayerInteractor> { PlayerInteractorImpl(get()) }
    viewModel { (previewUrl: String) -> PlayerViewModel(previewUrl, get()) }
}
