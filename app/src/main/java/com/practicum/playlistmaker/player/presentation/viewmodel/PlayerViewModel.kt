package com.practicum.playlistmaker.player.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.medialibrary.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.api.PlaylistsInteractor
import kotlinx.coroutines.launch
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist


class PlayerViewModel(
    private val previewUrl: String,
    private val interactor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {
    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState
    private val _isFavorite = MutableLiveData<Boolean>(false)
    val isFavorite: LiveData<Boolean> = _isFavorite
    private var currentTrack: Track? = null
    private val _playlists = MutableLiveData<List<com.practicum.playlistmaker.medialibrary.domain.model.Playlist>>()
    val playlists: LiveData<List<com.practicum.playlistmaker.medialibrary.domain.model.Playlist>> = _playlists
    data class ToastEvent(val messageResId: Int, val playlistName: String, val shouldDismiss: Boolean)
    private val _addToPlaylistResult = MutableLiveData<ToastEvent>()
    val addToPlaylistResult: LiveData<ToastEvent> = _addToPlaylistResult

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

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getAllPlaylists().collect { list ->
                _playlists.postValue(list)
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        val track = currentTrack ?: return
        viewModelScope.launch {
            val latest = playlistsInteractor.getPlaylistById(playlist.id.toLong()) ?: playlist
            val added = playlistsInteractor.addTrackToPlaylist(latest, track)
            if (added) {
                _addToPlaylistResult.postValue(ToastEvent(R.string.track_added_to_playlist, latest.name, true))
            } else {
                _addToPlaylistResult.postValue(ToastEvent(R.string.track_in_playlist_already, latest.name, false))
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