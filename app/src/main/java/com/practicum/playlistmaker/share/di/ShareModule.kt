package com.practicum.playlistmaker.share.di

import com.practicum.playlistmaker.share.data.ShareRepositoryImpl
import com.practicum.playlistmaker.share.domain.api.ShareFunctionsInteractor
import com.practicum.playlistmaker.share.domain.api.ShareRepository
import com.practicum.playlistmaker.share.domain.impl.ShareFunctionsInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val shareModule = module {
    factory<ShareRepository> { ShareRepositoryImpl(androidContext()) }
    factory<ShareFunctionsInteractor> { ShareFunctionsInteractorImpl(get()) }
}