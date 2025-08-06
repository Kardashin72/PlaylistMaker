package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface TracksSearchHistoryRepository {
    fun saveTrackToHistory(track: Track)
    fun loadSearchHistory(): List<Track>
    fun clearSearchHistory()
    fun hasHistory(): Boolean
}