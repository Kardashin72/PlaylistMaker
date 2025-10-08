package com.practicum.playlistmaker.player.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.medialibrary.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.medialibrary.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.domain.model.UiEvent
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch


class PlayerViewModel(
    private val previewUrl: String,
    private val interactor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {
    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState
    //пока не совсем понял, как уйти от этой переменной, в БД у нас только отмеченные треки (избранные, в плейлистах)
    //а плеер работает со всеми результатами поиска, которые еще могут быть не отмечены, подумаю на каникулах
    private var currentTrack: Track? = null


    init {
        interactor.preparePlayer(previewUrl) { state ->
            val current = _playerState.value
            _playerState.postValue(
                state.copy(
                    isFavorite = current?.isFavorite ?: false,
                    playlists = current?.playlists ?: emptyList(),
                    toastEvent = null
                )
            )
        }
    }

    fun setTrack(track: Track) {
        currentTrack = track
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracksId().collect { favoritesIds ->
                val favoriteNow = favoritesIds.contains(track.trackId)
                val current = _playerState.value ?: PlayerState()
                _playerState.postValue(current.copy(isFavorite = favoriteNow))
            }
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getAllPlaylists().collect { list ->
                val current = _playerState.value ?: PlayerState()
                _playerState.postValue(current.copy(playlists = list))
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        val track = currentTrack ?: return
        viewModelScope.launch {
            val latest = playlistsInteractor.getPlaylistById(playlist.id.toLong()) ?: playlist
            val added = playlistsInteractor.addTrackToPlaylist(latest, track)
            val event = if (added) UiEvent(R.string.track_added_to_playlist, latest.name, true)
            else UiEvent(R.string.track_in_playlist_already, latest.name, false)
            val current = _playerState.value ?: PlayerState()
            _playerState.postValue(current.copy(toastEvent = event))
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
            if (_playerState.value?.isFavorite == true) {
                favoritesInteractor.deleteTrackFromFavorites(track.trackId)
            } else {
                favoritesInteractor.addTrackToFavorites(track)
            }
        }
    }

    fun onToastEventHandled() {
        val current = _playerState.value ?: return
        _playerState.postValue(current.copy(toastEvent = null))
    }
}