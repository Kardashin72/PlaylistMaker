package com.practicum.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.android.ext.android.inject
import android.content.pm.ServiceInfo

class AudioPlayerService : Service(), AudioPlayerServiceApi {

    companion object {
        private const val CHANNEL_ID = "playback_channel"
        private const val NOTIFICATION_ID = 1
        const val EXTRA_PREVIEW_URL = "extra_preview_url"
        const val EXTRA_TRACK_NAME = "extra_track_name"
        const val EXTRA_ARTIST_NAME = "extra_artist_name"
    }

    private val playerInteractor: PlayerInteractor by inject()

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: StateFlow<PlayerState> = _playerState

    private var currentTrackName: String = ""
    private var currentArtistName: String = ""

    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        val previewUrl = intent?.getStringExtra(EXTRA_PREVIEW_URL).orEmpty()
        currentTrackName = intent?.getStringExtra(EXTRA_TRACK_NAME).orEmpty()
        currentArtistName = intent?.getStringExtra(EXTRA_ARTIST_NAME).orEmpty()
        if (previewUrl.isNotBlank()) {
            playerInteractor.preparePlayer(previewUrl) { state ->
                _playerState.value = state
                if (state.playerStatus !is PlayerState.PlayerStatus.Playing) {
                    hideForegroundNotification()
                }
            }
        }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerServiceApi = this@AudioPlayerService
    }

    override fun prepare(previewUrl: String, trackName: String, artistName: String) {
        currentTrackName = trackName
        currentArtistName = artistName
        playerInteractor.preparePlayer(previewUrl) { state ->
            _playerState.value = state
            if (state.playerStatus !is PlayerState.PlayerStatus.Playing) {
                hideForegroundNotification()
            }
        }
    }

    override fun play() {
        playerInteractor.startPlayer()
    }

    override fun pause() {
        playerInteractor.pausePlayer()
    }

    override fun release() {
        playerInteractor.releasePlayer()
        hideForegroundNotification()
        stopSelf()
    }

    override fun isPlaying(): Boolean {
        val state = _playerState.value.playerStatus
        return state is PlayerState.PlayerStatus.Playing
    }

    override fun showForegroundNotification() {
        val notification = buildNotification()
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            if (Build.VERSION.SDK_INT >= 29) ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK else 0
        )
    }

    override fun hideForegroundNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun buildNotification(): Notification {
        val contentText = if (currentArtistName.isNotBlank() || currentTrackName.isNotBlank()) {
            "$currentArtistName - $currentTrackName"
        } else {
            getString(R.string.app_name)
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(contentText)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Audio playback"
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }
}


