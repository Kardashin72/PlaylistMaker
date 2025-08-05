package com.practicum.playlistmaker.core.presentation.utils

fun trackTimeConvert(ms: Long): String {
    val minutes = ms / 1000 / 60
    val seconds = ms / 1000 % 60
    return String.format("%02d:%02d", minutes, seconds)
}