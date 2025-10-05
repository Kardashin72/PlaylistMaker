package com.practicum.playlistmaker.core.presentation.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var isClickAllowed = true
private var clickJob: Job? = null
private const val CLICK_DEBOUNCE_DELAY = 1000L

fun clickDebounce(scope: CoroutineScope) : Boolean {
    val current = isClickAllowed
    if (isClickAllowed) {
        isClickAllowed = false
        clickJob?.cancel()
        clickJob = scope.launch {
            delay(CLICK_DEBOUNCE_DELAY)
            isClickAllowed = true
        }
    }
    return current
}