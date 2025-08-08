package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.model.PlayerState


class PlayerRepositoryImpl : PlayerRepository {
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var onStateChanged: ((PlayerState) -> Unit)? = null

    override fun preparePlayer(previewUrl: String, onStateChanged: (PlayerState) -> Unit) {
        this.onStateChanged = onStateChanged
        mediaPlayer = MediaPlayer()
        mediaPlayer?.apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                onStateChanged(PlayerState(PlayerState.PlayerStatus.Prepared, 0))
            }
            setOnCompletionListener {
                onStateChanged(PlayerState(PlayerState.PlayerStatus.Prepared, 0))
                timerRunnable?.let { handler.removeCallbacks(it) }
                mediaPlayer?.seekTo(0)
            }
        }

    }

    override fun startPlayer() {
        mediaPlayer?.start()
        onStateChanged?.invoke(PlayerState(PlayerState.PlayerStatus.Playing, getCurrentPosition()))
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        onStateChanged?.invoke(PlayerState(PlayerState.PlayerStatus.Paused, getCurrentPosition()))
        timerRunnable?.let { handler.removeCallbacks(it) }
    }

    override fun releasePlayer() {
        timerRunnable?.let { handler.removeCallbacks(it) }
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    private fun startTimer() {
        timerRunnable = object : Runnable {
            override fun run() {
                val currentPosition = getCurrentPosition()
                if (mediaPlayer?.isPlaying == true) {
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