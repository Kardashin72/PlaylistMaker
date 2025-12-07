package com.practicum.playlistmaker.player.service

import com.practicum.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerServiceApi {
    fun prepare(previewUrl: String, trackName: String, artistName: String)
    fun play()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun showForegroundNotification()
    fun hideForegroundNotification()
    val playerState: StateFlow<PlayerState>
}


