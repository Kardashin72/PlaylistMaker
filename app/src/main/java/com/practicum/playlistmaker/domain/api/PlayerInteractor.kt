package com.practicum.playlistmaker.domain.api

interface PlayerInteractor {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun release()
}