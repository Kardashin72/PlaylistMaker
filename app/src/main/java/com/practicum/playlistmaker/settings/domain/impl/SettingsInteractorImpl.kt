package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun isDarkTheme(): Boolean = repository.isDarkTheme()
    override fun setDarkTheme(enabled: Boolean) = repository.setDarkTheme(enabled)
}