package com.practicum.playlistmaker.domain.impl

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.domain.api.PlayerInteractor

class PlayerInteractorImpl : PlayerInteractor {
    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler? = null
    private var timerRunnable: Runnable? = null
    private var isPrepared = false
    private var _isPlaying = false

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                isPrepared = true
                _isPlaying = false
                onPrepared()
            }
            setOnCompletionListener {
                _isPlaying = false
                onCompletion()
            }
        }
        handler = Handler(Looper.getMainLooper())
    }

    override fun play() {
        if (isPrepared && mediaPlayer != null) {
            mediaPlayer?.start()
            _isPlaying = true
        }
    }

    override fun pause() {
        if (isPrepared && mediaPlayer != null) {
            mediaPlayer?.pause()
            _isPlaying = false
        }
    }

    override fun getCurrentPosition(): Int {
        return if (isPrepared && mediaPlayer != null) mediaPlayer!!.currentPosition else 0
    }

    override fun isPlaying(): Boolean = _isPlaying

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        handler = null
        timerRunnable = null
        isPrepared = false
        _isPlaying = false
    }
}
