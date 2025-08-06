package com.practicum.playlistmaker.settings.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.share.domain.api.ShareFunctionsInteractor
import com.practicum.playlistmaker.share.domain.model.ContactSupportData
import com.practicum.playlistmaker.share.domain.model.ShareAppData
import com.practicum.playlistmaker.share.domain.model.UserAgreementData

data class SettingsScreenState(
    val isDarkTheme: Boolean = false,
    val shareAppData: ShareAppData? = null,
    val contactSupportData: ContactSupportData? = null,
    val userAgreementData: UserAgreementData? = null
)

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val shareFunctionsInteractor: ShareFunctionsInteractor
) : ViewModel() {
    private val _screenState = MutableLiveData<SettingsScreenState>()
    val screenState: LiveData<SettingsScreenState> = _screenState

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val isDarkTheme = settingsInteractor.isDarkTheme()
        _screenState.postValue(SettingsScreenState(isDarkTheme = isDarkTheme))
    }

    fun setDarkTheme(enabled: Boolean) {
        settingsInteractor.setDarkTheme(enabled)
        _screenState.postValue(_screenState.value?.copy(isDarkTheme = enabled))
    }

    fun shareApp() {
        val data = shareFunctionsInteractor.getShareData()
        _screenState.postValue(_screenState.value?.copy(shareAppData = data))
    }

    fun contactSupport() {
        val data = shareFunctionsInteractor.getSupportData()
        _screenState.postValue(_screenState.value?.copy(contactSupportData = data))
    }

    fun openUserAgreement() {
        val data = shareFunctionsInteractor.getUserAgreementData()
        _screenState.postValue(_screenState.value?.copy(userAgreementData = data))
    }

    fun clearShareData() {
        _screenState.postValue(_screenState.value?.copy(
            shareAppData = null,
            contactSupportData = null,
            userAgreementData = null
        ))
    }

    companion object {
        fun getFactory(
            settingsInteractor: SettingsInteractor,
            shareFunctionsInteractor: ShareFunctionsInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { SettingsViewModel(settingsInteractor, shareFunctionsInteractor) }
        }
    }

}