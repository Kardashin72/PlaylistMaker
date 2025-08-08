package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.model.PlayerState


class PlayerRepositoryImpl(
    private val mediaPLayer: MediaPlayer
) : PlayerRepository {
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
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
                timerRunnable?.let { handler.removeCallbacks(it) }
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
        timerRunnable?.let { handler.removeCallbacks(it) }
    }

    override fun releasePlayer() {
        timerRunnable?.let { handler.removeCallbacks(it) }
        mediaPLayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPLayer.currentPosition
    }

    private fun startTimer() {
        timerRunnable = object : Runnable {
            override fun run() {
                val currentPosition = getCurrentPosition()
                if (mediaPLayer.isPlaying == true) {
                    onStateChanged?.invoke(PlayerState(PlayerState.PlayerStatus.Playing, currentPosition))
                    handler.postDelayed(this, UPDATE_INTERVAL_MS)
                } else {
                    onStateChanged?.invoke(PlayerState(PlayerState.PlayerStatus.Paused, currentPosition))
                }
            }
        }
        handler.postDelayed(timerRunnable!!, UPDATE_INTERVAL_MS)
    }

    companion object {
        private const val UPDATE_INTERVAL_MS = 500L
    }
}