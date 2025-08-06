package com.practicum.playlistmaker.player.domain.model

data class PlayerState(
    val playerStatus: PlayerStatus = PlayerStatus.Default,
    val currentPosition: Int = 0
) {
    sealed class PlayerStatus {
        object Default : PlayerStatus()
        object Prepared : PlayerStatus()
        object Playing : PlayerStatus()
        object Paused : PlayerStatus()
    }
}