package com.practicum.playlistmaker.medialibrary.domain.model

import com.practicum.playlistmaker.search.domain.model.Track

data class PlaylistUiState(
    val isLoading: Boolean = true,
    val playlist: Playlist? = null,
    val durationMinutes: Int = 0,
    val tracks: List<Track> = emptyList()
)
