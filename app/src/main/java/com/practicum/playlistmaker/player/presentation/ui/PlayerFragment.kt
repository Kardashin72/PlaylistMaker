package com.practicum.playlistmaker.player.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.IntentCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.presentation.utils.dpToPx
import com.practicum.playlistmaker.core.presentation.utils.trackTimeConvert
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.presentation.ui.SearchActivity
import com.practicum.playlistmaker.search.presentation.ui.SearchFragment.Companion.TRACK_KEY
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment: Fragment() {
    private lateinit var binding: FragmentAudioPlayerBinding
    private lateinit var viewModel: PlayerViewModel
    private lateinit var previewUrl: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root

        val track = getTrackFromIntent()
        previewUrl = track?.previewUrl.toString()

        viewModel = getViewModel(parameters = { parametersOf(previewUrl) })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        bindTrackData(track)
        observeViewModel()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    //"очистка" плеера при уничтожении активити
    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            viewModel.onCleared()
        }
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
            binding.trackTimer.text = trackTimeConvert(state.currentPosition.toLong())
            when (state.playerStatus) {
                is PlayerState.PlayerStatus.Default -> {
                    binding.apply {
                        playButton.isEnabled = false
                        playButton.isVisible = true
                        pauseButton.visibility = View.INVISIBLE
                    }
                }
                is PlayerState.PlayerStatus.Prepared -> {
                    binding.apply {
                        playButton.isEnabled = true
                        playButton.isVisible = true
                        pauseButton.visibility = View.INVISIBLE
                    }
                }
                is PlayerState.PlayerStatus.Playing -> {
                    binding.apply {
                        playButton.visibility = View.INVISIBLE
                        pauseButton.isVisible = true
                    }
                }
                is PlayerState.PlayerStatus.Paused -> {
                    binding.apply {
                        playButton.isVisible = true
                        pauseButton.visibility = View.INVISIBLE
                    }
                }

            }
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

    companion object {
        private const val TRACK_KEY = "TRACK"
        fun createArgs(track: Track): Bundle = bundleOf(TRACK_KEY to track)
    }
}