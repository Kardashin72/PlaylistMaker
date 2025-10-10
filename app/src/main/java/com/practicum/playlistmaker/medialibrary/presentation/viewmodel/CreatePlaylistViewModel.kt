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
    data class UiState(
        val mode: Mode = Mode.CREATE,
        val editing: Playlist? = null
    )
    enum class Mode { CREATE, EDIT }

    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> = _state

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

    fun loadForEdit(playlistId: Long) {
        if (playlistId <= 0) return
        viewModelScope.launch {
            val existing = playlistsInteractor.getPlaylistById(playlistId) ?: return@launch
            _state.postValue(UiState(mode = Mode.EDIT, editing = existing))
        }
    }

    fun saveEdited(name: String, description: String, coverImagePath: String?) {
        val current = _state.value?.editing ?: return
        val updated = current.copy(
            name = name,
            description = description,
            coverImagePath = coverImagePath ?: current.coverImagePath
        )
        viewModelScope.launch {
            playlistsInteractor.updatePlaylist(updated)
            _event.postValue(UiEvent(R.string.playlist_saved_toast, updated.name, true))
        }
    }
}