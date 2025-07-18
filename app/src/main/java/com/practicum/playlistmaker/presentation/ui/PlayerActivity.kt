package com.practicum.playlistmaker.presentation.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.utils.dpToPx

class PlayerActivity : AppCompatActivity() {
    private var playerState = STATE_DEFAULT
    private lateinit var playerTopBar: MaterialToolbar
    private lateinit var trackArtwork: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var addToPlaylist: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var likeActiveButton: ImageButton
    private lateinit var trackTimer: TextView
    private lateinit var trackTime: TextView
    private lateinit var album: TextView
    private lateinit var releaseDate: TextView
    private lateinit var genreName: TextView
    private lateinit var country: TextView
    private lateinit var previewUrl: String
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val track = IntentCompat.getParcelableExtra(intent,
            SearchActivity.Companion.INTENT_TRACK_KEY, Track::class.java)

        playerTopBar = findViewById(R.id.audioplayer_topbar)
        trackArtwork = findViewById(R.id.player_track_artwork)
        trackName = findViewById(R.id.player_track_name)
        artistName = findViewById(R.id.player_artist_name)
        addToPlaylist = findViewById(R.id.add_to_playlist_button)
        playButton = findViewById(R.id.play_button)
        pauseButton = findViewById(R.id.pause_button)
        likeButton = findViewById(R.id.like_button)
        likeActiveButton = findViewById(R.id.like_button_active)
        trackTimer = findViewById(R.id.track_timer)
        trackTime = findViewById(R.id.track_time)
        album = findViewById(R.id.collection_name)
        releaseDate = findViewById(R.id.release_date)
        genreName = findViewById(R.id.primary_genre_name)
        country = findViewById(R.id.country)
        previewUrl = track?.previewUrl.toString()

        val artworkUrl = track?.artworkUrl100
        Glide.with(trackArtwork)
            .load(artworkUrl?.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(trackArtwork.context.dpToPx(8)))
            .into(trackArtwork)

        trackName.text = track?.trackName
        artistName.text = track?.artistName
        trackTimer.text = formatTime(0)
        trackTime.text = track?.trackTime
        album.text = track?.collectionName

        val index = track?.releaseDate?.indexOf('-')
        releaseDate.text = index?.let { track?.releaseDate?.substring(0, it) }
        genreName.text = track?.primaryGenreName
        country.text = track?.country


        playerTopBar.setNavigationOnClickListener {
            finish()
        }
        preparePlayer()
        playButton.setOnClickListener { startPlayer() }
        pauseButton.setOnClickListener { pausePlayer() }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
            playPauseVisibility()
            updateTimerUI(0)
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            playPauseVisibility()
            stopTimer()
            updateTimerUI(0)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playPauseVisibility()
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playPauseVisibility()
        stopTimer()
    }

    private fun playPauseVisibility() {
        if (playerState in 0..2) {
            pauseButton.visibility = View.INVISIBLE
            playButton.visibility = View.VISIBLE
        }
        else {
            pauseButton.visibility = View.VISIBLE
            playButton.visibility = View.INVISIBLE
        }
    }

    private fun startTimer() {
        stopTimer()
        timerRunnable = object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    val currentPosition = mediaPlayer.currentPosition
                    if (currentPosition >= FRAGMENT_DURATION) {
                        mediaPlayer.pause()
                        mediaPlayer.seekTo(0)
                        playerState = STATE_PAUSED
                        playPauseVisibility()
                        stopTimer()
                        updateTimerUI(0)
                    } else {
                        updateTimerUI(currentPosition)
                        handler.postDelayed(this, 500)
                    }
                }
            }
        }.also(handler::post)
    }

    private fun stopTimer() {
        timerRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun updateTimerUI(positionMs: Int) {
        trackTimer.text = formatTime(positionMs)
    }

    private fun formatTime(positionMs: Int): String {
        val totalSeconds = positionMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PAUSED = 2
        private const val STATE_PLAYING = 3
        private const val FRAGMENT_DURATION = 30_000
    }
}