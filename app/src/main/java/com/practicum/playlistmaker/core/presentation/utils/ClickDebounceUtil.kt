package com.practicum.playlistmaker.core.presentation.utils

import android.os.SystemClock

private var lastClickAtMs: Long = 0L
private const val CLICK_DEBOUNCE_DELAY = 1000L

fun clickDebounce() : Boolean {
    val now = SystemClock.elapsedRealtime()
    return if (now - lastClickAtMs >= CLICK_DEBOUNCE_DELAY) {
        lastClickAtMs = now
        true
    } else {
        false
    }
}