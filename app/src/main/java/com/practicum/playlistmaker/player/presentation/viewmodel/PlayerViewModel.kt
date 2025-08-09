package com.practicum.playlistmaker.player.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState


class PlayerViewModel(
    private val previewUrl: String,
    private val interactor: PlayerInteractor
) : ViewModel() {
    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    init {
        interactor.preparePlayer(previewUrl) { state ->
            _playerState.postValue(state)
        }
    }
    fun startPlayer() {
        interactor.startPlayer()
    }

    fun pausePlayer() {
        interactor.pausePlayer()
    }

    fun getCurrentPosition(): Int {
        return interactor.getCurrentPosition()
    }

    public override fun onCleared() {
        super.onCleared()
        interactor.releasePlayer()
    }

    companion object {
        private const val FRAGMENT_DURATION = 30_000
    }
}