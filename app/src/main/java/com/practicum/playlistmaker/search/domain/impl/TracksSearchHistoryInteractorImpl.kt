package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TracksSearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksSearchHistoryRepository
import com.practicum.playlistmaker.search.domain.model.Track

class TracksSearchHistoryInteractorImpl(private val repository: TracksSearchHistoryRepository) :
    TracksSearchHistoryInteractor {
    override fun saveTrackToHistory(track: Track) = repository.saveTrackToHistory(track)
    override fun loadSearchHistory(): List<Track> = repository.loadSearchHistory()
    override fun clearSearchHistory() = repository.clearSearchHistory()
    override fun hasHistory(): Boolean = repository.hasHistory()
}