package com.practicum.playlistmaker.player.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.search.presentation.ui.SearchActivity
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.core.presentation.utils.dpToPx
import com.practicum.playlistmaker.core.presentation.utils.trackTimeConvert
import com.practicum.playlistmaker.player.presentation.viewmodel.PlayerState
import com.practicum.playlistmaker.player.presentation.viewmodel.PlayerViewModel

class PlayerActivity : AppCompatActivity() {

    private lateinit var previewUrl: String
    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = getTrackFromIntent()
        previewUrl = track?.previewUrl.toString()

        setupViewModel()
        setupClickListeners()
        bindTrackData(track)
        observeViewModel()
    }

    //пауза плеера при приостановке активити
    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    //"очистка" плеера при уничтожении активити
    override fun onDestroy() {
        super.onDestroy()
        viewModel.onCleared()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getFactory(previewUrl)
        )[PlayerViewModel::class.java]
    }

    private fun setupClickListeners() {
        //обработка нажатия на кнопку "назад"
        binding.audioPlayerTopbar.setNavigationOnClickListener {
            finish()
        }
        //обработка нажатия на кнопку плей/пауза
        binding.playButton.setOnClickListener { viewModel.startPlayer() }
        binding.pauseButton.setOnClickListener { viewModel.pausePlayer() }
    }

    private fun observeViewModel() {
        viewModel.playerState.observe(this, Observer { state ->
            when (state) {
                is PlayerState.Default -> {
                    binding.apply {
                        playButton.isEnabled = false
                        playButton.visibility = View.VISIBLE
                        pauseButton.visibility = View.INVISIBLE
                    }
                }
                is PlayerState.Prepared -> {
                    binding.apply {
                        playButton.isEnabled = true
                        playButton.visibility = View.VISIBLE
                        pauseButton.visibility = View.INVISIBLE
                    }
                }
                is PlayerState.Playing -> {
                    binding.apply {
                        playButton.visibility = View.INVISIBLE
                        pauseButton.visibility = View.VISIBLE
                    }
                }
                is PlayerState.Paused -> {
                    binding.apply {
                        playButton.visibility = View.VISIBLE
                        pauseButton.visibility = View.INVISIBLE
                    }
                }

            }
        })

        viewModel.currentPosition.observe(this, Observer {currentTime ->
            binding.trackTimer.text = trackTimeConvert(currentTime.toLong())
        })
    }

    private fun getTrackFromIntent(): Track? {
        return IntentCompat.getParcelableExtra(
            intent,
            SearchActivity.INTENT_TRACK_KEY,
            Track::class.java
        )
    }

    private fun bindTrackData(track: Track?) {
        //загрузка обложки трека в view
        val artworkUrl = track?.artworkUrl100
        Glide.with(binding.playerTrackArtwork)
            .load(artworkUrl?.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(binding.playerTrackArtwork.context.dpToPx(8)))
            .into(binding.playerTrackArtwork)

        //передача данных трека в view
        binding.apply {
            playerTrackName.text = track?.trackName
            playerArtistName.text = track?.artistName
            trackTimer.text = trackTimeConvert(0)
            trackTime.text = track?.trackTime
            collectionName.text = track?.collectionName
            val index = track?.releaseDate?.indexOf('-')
            releaseDate.text = index?.let { track?.releaseDate?.substring(0, it) }
            primaryGenreName.text = track?.primaryGenreName
            country.text = track?.country
        }
    }
}