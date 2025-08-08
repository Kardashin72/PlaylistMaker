package com.practicum.playlistmaker.search.domain.api

interface TracksSearchInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(result: SearchResult)
    }
}