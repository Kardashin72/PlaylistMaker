package com.practicum.playlistmaker.UI

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.UI.SearchActivity.Companion.INTENT_TRACK_KEY
import com.practicum.playlistmaker.data.Track
import com.practicum.playlistmaker.utils.dpToPx

class PlayerActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val track = IntentCompat.getParcelableExtra(intent, INTENT_TRACK_KEY, Track::class.java)

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

        val artworkUrl = track?.artworkUrl100
        Glide.with(trackArtwork)
            .load(artworkUrl?.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(trackArtwork.context.dpToPx(8)))
            .into(trackArtwork)

        trackName.text = track?.trackName
        artistName.text = track?.artistName
        trackTimer.text = track?.trackTime
        trackTime.text = track?.trackTime
        album.text = track?.collectionName

        val index = track?.releaseDate?.indexOf('-')
        releaseDate.text = index?.let { track.releaseDate.substring(0, it) }
        genreName.text = track?.primaryGenreName
        country.text = track?.country

        playerTopBar.setNavigationOnClickListener {
            finish()
        }
    }
}