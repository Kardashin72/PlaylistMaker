package com.practicum.playlistmaker.search.di

import com.practicum.playlistmaker.search.domain.api.TracksSearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.search.domain.impl.TracksSearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksSearchInteractorImpl
import org.koin.dsl.module

val searchDomainModule = module {
    factory<TracksSearchInteractor> { TracksSearchInteractorImpl(get()) }
    factory<TracksSearchHistoryInteractor> { TracksSearchHistoryInteractorImpl(get()) }
}