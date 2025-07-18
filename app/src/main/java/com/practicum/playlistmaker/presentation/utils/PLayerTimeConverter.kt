package com.practicum.playlistmaker.presentation.utils


fun convertPLayerTime(positionMs: Int): String {
    val totalSeconds = positionMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}