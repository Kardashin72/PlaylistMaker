package com.practicum.playlistmaker.player.presentation.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

sealed class PlayerState {
    object Default : PlayerState()
    object Prepared : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
}

class PlayerViewModel(private val previewUrl: String) : ViewModel() {
    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default)
    val playerState: LiveData<PlayerState> = _playerState
    private val _currentPosition = MutableLiveData(0)
    val currentPosition: LiveData<Int> = _currentPosition

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null

    init {
        preparePlayer()
    }

    private fun startTimer() {
        stopTimer()
        timerRunnable = object : Runnable {
            override fun run() {
                if (playerState.value is PlayerState.Playing) {
                    val currentTime = getCurrentPosition()
                    _currentPosition.postValue(currentTime)
                    if (currentTime >= FRAGMENT_DURATION) {
                        pausePlayer()
                        stopTimer()
                        _currentPosition.postValue(0)
                    } else {
                        handler.postDelayed(this, 500)
                    }
                }
            }
        }.also(handler::post)
    }

    private fun stopTimer() {
        timerRunnable?.let { handler.removeCallbacks(it) }
        timerRunnable = null
    }

    private fun preparePlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                _playerState.postValue(PlayerState.Prepared)
                _currentPosition.postValue(0)
            }
            setOnCompletionListener {
                _playerState.postValue(PlayerState.Prepared)
                _currentPosition.postValue(0)
                stopTimer()
            }
        }
    }

    fun startPlayer() {
        mediaPlayer?.start()
        _playerState.postValue(PlayerState.Playing)
        startTimer()

    }

    fun pausePlayer() {
        mediaPlayer?.pause()
        _playerState.postValue(PlayerState.Paused)
        stopTimer()

    }

    private fun getCurrentPosition(): Int {
        return if (playerState.value != PlayerState.Default) mediaPlayer!!.currentPosition else 0
    }

    public override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
        stopTimer()
        _playerState.postValue(PlayerState.Default)
    }

    companion object {
        private const val FRAGMENT_DURATION = 30_000

        fun getFactory(previewUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(previewUrl)
            }
        }
    }
}