package com.practicum.playlistmaker.medialibrary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val playlistId: Long
): ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val playlist: Playlist? = null,
        val durationMinutes: Int = 0,
        val tracks: List<Track> = emptyList()
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        load()
        viewModelScope.launch {
            state
                .map { it.playlist?.trackIds ?: emptyList() }
                .distinctUntilChanged()
                .flatMapLatest { ids -> playlistsInteractor.getTracksByIds(ids) }
                .collect { tracks ->
                    val orderIds: List<Int> = _state.value.playlist?.trackIds ?: emptyList()
                    val idToTrack = tracks.associateBy { it.trackId }
                    val ordered = if (orderIds.isNotEmpty()) orderIds.asReversed().mapNotNull { idToTrack[it] } else tracks
                    val sortedTracks: List<Track> = if (ordered.isEmpty() && tracks.isNotEmpty()) tracks else ordered

                    val totalSeconds = sortedTracks.sumOf { track ->
                        val parts = track.trackTime.split(":")
                        val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
                        val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
                        minutes * 60 + seconds
                    }
                    val minutesTotal = totalSeconds / 60
                    _state.value = _state.value.copy(durationMinutes = minutesTotal, tracks = sortedTracks)
                }
        }
    }

    private fun load() {
        viewModelScope.launch {
            val loaded = playlistsInteractor.getPlaylistById(playlistId)
            if (loaded == null) {
                _state.value = UiState(isLoading = false, playlist = null, durationMinutes = 0)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = false, playlist = loaded)
        }
    }

    fun reload() {
        load()
    }

    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            playlistsInteractor.removeTrackFromPlaylist(playlistId, trackId)
            val loaded = playlistsInteractor.getPlaylistById(playlistId) ?: return@launch
            _state.value = _state.value.copy(playlist = loaded)
        }
    }

    fun deletePlaylist(onComplete: () -> Unit) {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlistId)
            onComplete()
        }
    }

    fun buildShareText(): String? {
        val current = _state.value
        val playlist = current.playlist ?: return null
        if (current.tracks.isEmpty()) return ""
        val sb = StringBuilder()
        sb.appendLine(playlist.name)
        if (playlist.description.isNotBlank()) sb.appendLine(playlist.description)
        sb.appendLine(String.format("%d треков", playlist.tracksCount))
        current.tracks.forEachIndexed { index, track ->
            sb.appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})")
        }
        return sb.toString().trimEnd()
    }
}


