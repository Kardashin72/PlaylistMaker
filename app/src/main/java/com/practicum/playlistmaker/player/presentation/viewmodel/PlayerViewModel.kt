package com.practicum.playlistmaker.player.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.medialibrary.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class PlayerViewModel(
    private val previewUrl: String,
    private val interactor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
) : ViewModel() {
    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState
    private val _isFavorite = MutableLiveData<Boolean>(false)
    val isFavorite: LiveData<Boolean> = _isFavorite
    private var currentTrack: Track? = null

    init {
        interactor.preparePlayer(previewUrl) { state ->
            _playerState.postValue(state)
        }
    }

    fun setTrack(track: Track) {
        currentTrack = track
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracksId().collect { favoritesIds ->
                _isFavorite.postValue(favoritesIds.contains(track.trackId))
            }
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

    fun releasePlayer() {
        interactor.releasePlayer()
    }

    fun onLikeClicked() {
        val track = currentTrack ?: return
        viewModelScope.launch {
            if (_isFavorite.value == true) {
                favoritesInteractor.deleteTrackFromFavorites(track.trackId)
            } else {
                favoritesInteractor.addTrackToFavorites(track)
            }
        }
    }
}