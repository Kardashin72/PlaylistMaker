package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

sealed class SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult()
    object ConnectionError : SearchResult()
}

interface TracksSearchRepository {
    fun searchTracks(expression: String): SearchResult
}