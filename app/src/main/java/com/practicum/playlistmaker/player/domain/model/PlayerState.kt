package com.practicum.playlistmaker.player.domain.model

data class PlayerState(
    val playerStatus: PlayerStatus = PlayerStatus.Default,
    val currentPosition: Int = 0,
    val isFavorite: Boolean = false,
    val playlists: List<com.practicum.playlistmaker.medialibrary.domain.model.Playlist> = emptyList(),
    val toastEvent: UiEvent? = null
) {
    sealed class PlayerStatus {
        object Default : PlayerStatus()
        object Prepared : PlayerStatus()
        object Playing : PlayerStatus()
        object Paused : PlayerStatus()
    }
}

data class UiEvent(
    val messageResId: Int,
    val playlistName: String,
    val shouldDismiss: Boolean
)