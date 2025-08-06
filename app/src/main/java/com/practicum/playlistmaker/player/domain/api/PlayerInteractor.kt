package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.model.PlayerState

interface PlayerInteractor {
    fun preparePlayer(url: String, onStateChanged: (PlayerState) -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
}