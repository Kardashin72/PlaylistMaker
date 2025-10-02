package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

sealed class SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult()
    object ConnectionError : SearchResult()
}

interface TracksSearchRepository {
    fun searchTracks(expression: String): Flow<SearchResult>
}