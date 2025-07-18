package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

sealed class SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult()
    object ConnectionError : SearchResult()
}

interface TracksSearchRepository {
    fun searchTracks(expression: String): SearchResult
}