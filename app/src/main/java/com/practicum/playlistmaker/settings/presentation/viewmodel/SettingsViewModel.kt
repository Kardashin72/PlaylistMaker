package com.practicum.playlistmaker.settings.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.core.presentation.utils.SingleLiveEvent
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.share.domain.api.ShareFunctionsInteractor
import com.practicum.playlistmaker.share.domain.model.ContactSupportData
import com.practicum.playlistmaker.share.domain.model.ShareAppData
import com.practicum.playlistmaker.share.domain.model.UserAgreementData

data class SettingsScreenState(
    val isDarkTheme: Boolean = false,
)

sealed class SettingsAction {
    data class ShareApp(val data: ShareAppData) : SettingsAction()
    data class ContactSupport(val data: ContactSupportData) : SettingsAction()
    data class OpenUserAgreement(val data: UserAgreementData) : SettingsAction()
}

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val shareFunctionsInteractor: ShareFunctionsInteractor
) : ViewModel() {
    private val _screenState = MutableLiveData<SettingsScreenState>()
    val screenState: LiveData<SettingsScreenState> = _screenState

    private val _action = SingleLiveEvent<SettingsAction>()
    val action: LiveData<SettingsAction> = _action

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val isDarkTheme = settingsInteractor.isDarkTheme()
        _screenState.postValue(SettingsScreenState(isDarkTheme = isDarkTheme))
    }

    fun setDarkTheme(enabled: Boolean) {
        settingsInteractor.setDarkTheme(enabled)
        _screenState.postValue(SettingsScreenState(isDarkTheme = enabled))
    }

    fun shareApp() {
        _action.value = SettingsAction.ShareApp(shareFunctionsInteractor.getShareData())
    }

    fun contactSupport() {
        _action.value = SettingsAction.ContactSupport(shareFunctionsInteractor.getSupportData())
    }

    fun openUserAgreement() {
        _action.value = SettingsAction.OpenUserAgreement(shareFunctionsInteractor.getUserAgreementData())
    }
}