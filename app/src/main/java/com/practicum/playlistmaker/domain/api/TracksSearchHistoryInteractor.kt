package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksSearchHistoryInteractor {
    fun saveTrackToHistory(track: Track)
    fun loadSearchHistory(): List<Track>
    fun clearSearchHistory()
    fun hasHistory(): Boolean
}