package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.search.domain.api.TracksSearchRepository
import com.practicum.playlistmaker.search.domain.api.SearchResult
import kotlinx.coroutines.flow.Flow

class TracksSearchInteractorImpl(private val repository: TracksSearchRepository) :
    TracksSearchInteractor {
    override fun searchTracks(expression: String): Flow<SearchResult> =
        repository.searchTracks(expression)
}