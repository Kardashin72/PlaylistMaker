package com.practicum.playlistmaker.medialibrary.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.medialibrary.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
): ViewModel() {

    data class UiEvent(val messageResId: Int, val playlistName: String, val shouldClose: Boolean)

    private val _event = MutableLiveData<UiEvent?>()
    val event: LiveData<UiEvent?> = _event

    fun createPlaylist(name: String, description: String, coverImagePath: String?) {
        val playlist = Playlist(
            name = name,
            description = description,
            coverImagePath = coverImagePath,
            trackIds = emptyList(),
            tracksCount = 0
        )
        viewModelScope.launch {
            playlistsInteractor.addPlaylist(playlist)
            _event.postValue(UiEvent(R.string.playlist_created_toast, name, true))
        }
    }

    fun onEventHandled() {
        _event.postValue(null)
    }
}