package com.practicum.playlistmaker.medialibrary.domain.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverImagePath: String?,
    val trackIds: List<Int> = emptyList(),
    val tracksCount: Int = 0,
)