package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.domain.api.TracksSearchRepository
import java.util.concurrent.Executors

class TracksSearchInteractorImpl(private val repository: TracksSearchRepository) : TracksSearchInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksSearchInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
        }
    }
}