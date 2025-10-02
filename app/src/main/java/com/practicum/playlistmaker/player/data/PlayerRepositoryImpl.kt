package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class PlayerRepositoryImpl(
    private val mediaPLayer: MediaPlayer
) : PlayerRepository {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timerJob: Job? = null
    private var onStateChanged: ((PlayerState) -> Unit)? = null

    override fun preparePlayer(previewUrl: String, onStateChanged: (PlayerState) -> Unit) {
        this.onStateChanged = onStateChanged
        mediaPLayer.apply {
            reset()
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                onStateChanged(PlayerState(PlayerState.PlayerStatus.Prepared, 0))
            }
            setOnCompletionListener {
                onStateChanged(PlayerState(PlayerState.PlayerStatus.Prepared, 0))
                timerJob?.cancel()
                mediaPLayer.seekTo(0)
            }
        }

    }

    override fun startPlayer() {
        mediaPLayer.start()
        onStateChanged?.invoke(PlayerState(PlayerState.PlayerStatus.Playing, getCurrentPosition()))
        startTimer()
    }

    override fun pausePlayer() {
        mediaPLayer.pause()
        onStateChanged?.invoke(PlayerState(PlayerState.PlayerStatus.Paused, getCurrentPosition()))
        timerJob?.cancel()
    }

    override fun releasePlayer() {
        timerJob?.cancel()
        scope.cancel()
        mediaPLayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPLayer.currentPosition
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive) {
                val currentPosition = getCurrentPosition()
                if (mediaPLayer.isPlaying) {
                    onStateChanged?.invoke(PlayerState(PlayerState.PlayerStatus.Playing, currentPosition))
                    delay(UPDATE_INTERVAL_MS)
                } else {
                    onStateChanged?.invoke(PlayerState(PlayerState.PlayerStatus.Paused, currentPosition))
                    break
                }
            }
        }
    }

    companion object {
        private const val UPDATE_INTERVAL_MS = 300L
    }
}