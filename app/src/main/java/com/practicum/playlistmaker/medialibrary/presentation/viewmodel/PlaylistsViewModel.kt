package com.practicum.playlistmaker.medialibrary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
): ViewModel() {
    private val _state = MutableStateFlow<List<Playlist>>(emptyList())
    val state: StateFlow<List<Playlist>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            playlistsInteractor.getAllPlaylists().collect { list ->
                _state.value = list
            }
        }
    }
}