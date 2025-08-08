package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.model.PlayerState

interface PlayerRepository {
    fun preparePlayer(previewUrl: String, onStateChanged: (PlayerState) -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
}

