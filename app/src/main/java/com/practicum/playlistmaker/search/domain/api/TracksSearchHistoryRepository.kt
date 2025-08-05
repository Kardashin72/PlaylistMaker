package com.practicum.playlistmaker.search.domain.api

import com.bumptech.glide.load.engine.Resource
import com.practicum.playlistmaker.search.domain.model.Track

interface TracksSearchHistoryRepository {
    fun saveTrackToHistory(track: Track)
    fun loadSearchHistory(): List<Track>
    fun clearSearchHistory()
    fun hasHistory(): Boolean
}