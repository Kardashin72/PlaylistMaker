package com.practicum.playlistmaker.player.presentation.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.practicum.playlistmaker.player.service.AudioPlayerService
import com.practicum.playlistmaker.player.service.AudioPlayerServiceApi
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.presentation.utils.dpToPx
import com.practicum.playlistmaker.core.presentation.utils.trackTimeConvert
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.CreatePlaylistViewModel
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.presentation.ui.adapter.PlaylistsBottomSheetAdapter
import com.practicum.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var createPlaylistViewModel: CreatePlaylistViewModel
    private lateinit var previewUrl: String
    private val args: PlayerFragmentArgs by navArgs()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetAdapter: PlaylistsBottomSheetAdapter

    private var serviceBound: Boolean = false
    private var serviceApi: AudioPlayerServiceApi? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? AudioPlayerService.LocalBinder ?: return
            serviceApi = binder.getService()
            playerViewModel.addService(serviceApi!!)
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceApi = null
            serviceBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val track = args.track
        previewUrl = track.previewUrl.toString()
        playerViewModel = getViewModel(parameters = { parametersOf(previewUrl) })
        playerViewModel.setTrack(track)
        setupClickListeners()
        bindTrackData(track)
        observeViewModel()
        setupBottomSheetRecyclerView()

        val intent = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra(AudioPlayerService.EXTRA_PREVIEW_URL, previewUrl)
            putExtra(AudioPlayerService.EXTRA_TRACK_NAME, track.trackName)
            putExtra(AudioPlayerService.EXTRA_ARTIST_NAME, track.artistName)
        }
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStart() {
        super.onStart()
        playerViewModel.onUiStarted()
    }

    override fun onStop() {
        super.onStop()
        playerViewModel.onUiStopped(areNotificationsAllowed())
    }

    //"очистка" плеера при закрытии фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        if (serviceBound) {
            requireContext().unbindService(serviceConnection)
            serviceBound = false
        }
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.releasePlayer()
    }

    private fun setupClickListeners() {
        //обработка нажатия на кнопку "назад"
        binding.audioPlayerTopbar.setNavigationOnClickListener { findNavController().navigateUp() }
        //обработка нажатия на кнопку плей/пауза
        binding.playbackButton.setOnClickListener {
            if (binding.playbackButton.isPlaying()) {
                playerViewModel.startPlayer()
            } else {
                playerViewModel.pausePlayer()
            }
        }
        binding.likeButton.setOnClickListener { playerViewModel.onLikeClicked() }
        binding.likeButtonActive.setOnClickListener { playerViewModel.onLikeClicked() }
        binding.addToPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            playerViewModel.loadPlaylists()
        }
        binding.root.findViewById<View>(R.id.new_playlist_button).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.createPlaylistFragment)
        }
    }

    private fun observeViewModel() {
        playerViewModel.playerState.observe(viewLifecycleOwner, Observer { state ->
            binding.trackTimer.text = trackTimeConvert(state.currentPosition.toLong())
            binding.likeButton.visibility = if (state.isFavorite) View.GONE else View.VISIBLE
            binding.likeButtonActive.visibility = if (state.isFavorite) View.VISIBLE else View.GONE
            when (state.playerStatus) {
                is PlayerState.PlayerStatus.Default -> {
                    binding.playbackButton.isEnabled = false
                    binding.playbackButton.setPlaying(false)
                }

                is PlayerState.PlayerStatus.Prepared -> {
                    binding.playbackButton.isEnabled = true
                    binding.playbackButton.setPlaying(false)
                }

                is PlayerState.PlayerStatus.Playing -> {
                    binding.playbackButton.isEnabled = true
                    binding.playbackButton.setPlaying(true)
                }

                is PlayerState.PlayerStatus.Paused -> {
                    binding.playbackButton.isEnabled = true
                    binding.playbackButton.setPlaying(false)
                }
            }
        })
        playerViewModel.playerState.observe(viewLifecycleOwner) { state ->
            bottomSheetAdapter.playlists = state.playlists
            bottomSheetAdapter.notifyDataSetChanged()

            state.toastEvent?.let {
                Toast.makeText(
                    requireContext(),
                    getString(it.messageResId, it.playlistName),
                    Toast.LENGTH_SHORT
                ).show()
                if (it.shouldDismiss) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                playerViewModel.onToastEventHandled()
            }
        }
    }

    private fun setupBottomSheetRecyclerView() {
        val bottomSheet = binding.root.findViewById<View>(R.id.playlists_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        val overlay = binding.root.findViewById<View>(R.id.overlay)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val alpha = ((slideOffset + 1f) / 2f).coerceIn(0f, 1f)
                overlay.alpha = alpha
            }
        })

        val recycler = bottomSheet.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.playlists_list)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        bottomSheetAdapter = PlaylistsBottomSheetAdapter(emptyList()) { playlist ->
            playerViewModel.addTrackToPlaylist(playlist)
        }
        recycler.adapter = bottomSheetAdapter
    }

    private fun bindTrackData(track: Track) {
        //загрузка обложки трека в view
        val artworkUrl = track?.artworkUrl100
        Glide.with(binding.playerTrackArtwork)
            .load(artworkUrl?.replaceAfterLast('/', "512x512bb.jpg"))
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

    private fun areNotificationsAllowed(): Boolean {
        return if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
        }
    }
}