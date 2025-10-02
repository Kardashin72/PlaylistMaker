package com.practicum.playlistmaker.search.domain.api

import kotlinx.coroutines.flow.Flow

interface TracksSearchInteractor {
    fun searchTracks(expression: String): Flow<SearchResult>
}