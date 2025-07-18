package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun isDarkTheme(): Boolean = repository.isDarkTheme()
    override fun setDarkTheme(enabled: Boolean) = repository.setDarkTheme(enabled)
}