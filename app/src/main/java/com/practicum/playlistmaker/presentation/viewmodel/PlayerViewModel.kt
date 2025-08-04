package com.practicum.playlistmaker.presentation.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.domain.api.PlayerInteractor

sealed class PlayerState {
    object Default: PlayerState()
    object Prepared: PlayerState()
    object Playing: PlayerState()
    object Paused: PlayerState()
}

class PlayerViewModel(private val previewUrl: String): ViewModel() {
    private val playerInteractor: PlayerInteractor by lazy {
        Creator.providePlayerInteractor()
    }
    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default)
    val playerState: LiveData<PlayerState> = _playerState
    private val _currentPosition = MutableLiveData(0)
    val currentPosition: LiveData<Int> = _currentPosition

    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null

    init {
        preparePlayer()
    }

    fun startTimer() {
        stopTimer()
        timerRunnable = object : Runnable {
            override fun run() {
                if (playerState.value is PlayerState.Playing && playerInteractor.isPlaying()) {
                    val currentTime = playerInteractor.getCurrentPosition()
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

    fun stopTimer() {
        timerRunnable?.let { handler.removeCallbacks(it) }
        timerRunnable = null
    }

    fun preparePlayer() {
        _playerState.value = PlayerState.Default
        playerInteractor.preparePlayer(previewUrl,
            onPrepared = {
                _playerState.postValue(PlayerState.Prepared)
                _currentPosition.postValue(0)
            },
            onCompletion = {
                _playerState.postValue(PlayerState.Prepared)
                _currentPosition.postValue(0)
                stopTimer()
            }
        )
    }

    fun startPlayer() {
        playerInteractor.play()
        _playerState.postValue(PlayerState.Playing)
        startTimer()
    }

    fun pausePlayer() {
        playerInteractor.pause()
        _playerState.postValue(PlayerState.Paused)
        stopTimer()
    }

    fun release() {
        playerInteractor.release()
        stopTimer()
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