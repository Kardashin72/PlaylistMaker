package com.practicum.playlistmaker.medialibrary.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.presentation.utils.dpToPx
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.PlaylistViewModel
import com.practicum.playlistmaker.search.presentation.ui.SearchRecycleViewAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val args: PlaylistFragmentArgs by navArgs()
    private val viewModel: PlaylistViewModel by viewModel { parametersOf(args.playlistId) }
    private lateinit var adapter: SearchRecycleViewAdapter
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playlistToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        observeState()
        setupRecycler()
        setupBottomSheet()
        setupMenu()
        setupClickListeners()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                val playlist = state.playlist
                binding.playlistTitle.text = playlist?.name ?: ""
                binding.playlistDescription.text = playlist?.description ?: ""

                adapter.tracks = state.tracks
                adapter.notifyDataSetChanged()

                binding.tracksRecycler.isVisible = state.tracks.isNotEmpty()
                binding.emptyTracksMessage.isVisible = state.tracks.isEmpty()

                if (state.tracks.isNotEmpty() && ::tracksBottomSheetBehavior.isInitialized) {
                    // гарантируем, что лист треков виден, а затемнение скрыто
                    tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    binding.root.findViewById<View>(R.id.overlay).visibility = View.GONE
                }

                binding.tracksDuration.text = resources.getQuantityString(
                    R.plurals.minutes_count,
                    state.durationMinutes,
                    state.durationMinutes
                )

                val count = playlist?.tracksCount ?: 0
                binding.tracksCount.text =
                    resources.getQuantityString(R.plurals.tracks_count, count, count)

                val coverPath = playlist?.coverImagePath
                if (coverPath.isNullOrBlank()) {
                    binding.playlistCover.setImageResource(R.drawable.placeholder)
                } else {
                    Glide.with(binding.playlistCover)
                        .load(coverPath)
                        .transform(CenterCrop())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(binding.playlistCover)
                }
                val menuItem = binding.root.findViewById<View>(R.id.menuPlaylistItem)
                val menuCover = menuItem.findViewById<ShapeableImageView>(R.id.imageCover)
                if (coverPath.isNullOrBlank()) {
                    menuCover.setImageResource(R.drawable.playlist_cover_placeholder)
                } else {
                    Glide.with(menuCover)
                        .load(coverPath)
                        .transform(CenterCrop())
                        .placeholder(R.drawable.playlist_cover_placeholder)
                        .error(R.drawable.playlist_cover_placeholder)
                        .into(menuCover)
                }
                menuItem.findViewById<TextView>(R.id.textName).text = playlist?.name ?: ""
                menuItem.findViewById<TextView>(R.id.textCount).text =
                    resources.getQuantityString(R.plurals.tracks_count, count, count)
            }
        }
    }

    private fun setupBottomSheet() {
        val bottomSheet = binding.root.findViewById<View>(R.id.tracks_bottom_sheet)
        tracksBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheet.post {
            //у меня на смартфоне, если делать как в макете фиксировано 266dp - треки наезжают на вышестоящие иконки,
            //поэтому я ограничивал их пик 214dp, вот сейчас с такой реализацией у меня вроде как все норм
            val parentHeight = binding.root.height
            val iconsBottom = binding.menuButton.bottom
            val safeMargin = requireContext().dpToPx(16)
            val desiredPeek = requireContext().dpToPx(266)
            val maxPeek = (parentHeight - iconsBottom - safeMargin).coerceAtLeast(0)
            tracksBottomSheetBehavior.peekHeight = desiredPeek.coerceAtMost(maxPeek)
            tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun showOverlayedDialog(build: MaterialAlertDialogBuilder.() -> Unit) {
        val overlay = binding.root.findViewById<View>(R.id.overlay)
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AppAlertDialogTheme)
            .apply(build)
            .create()
        dialog.setOnShowListener {
            overlay.alpha = 1f
            overlay.visibility = View.VISIBLE
        }
        dialog.setOnDismissListener {
            overlay.visibility = View.GONE
        }
        dialog.show()
    }

    private fun setupRecycler() {
        adapter = SearchRecycleViewAdapter(
            emptyList(),
            onItemClick = { track ->
                val direction =
                    PlaylistFragmentDirections.actionPlaylistFragmentToPlayerFragment(track)
                findNavController().navigate(direction)
            }
        )
        val recycler = binding.tracksRecycler
        recycler.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        recycler.adapter = adapter
        adapter.setOnItemLongClickListener { track ->
            showOverlayedDialog {
                setMessage(getString(R.string.delete_track_dialog_message))
                setNegativeButton(getString(R.string.no)) { dialogInterface, _ -> dialogInterface.dismiss() }
                setPositiveButton(getString(R.string.yes)) { dialogInterface, _ ->
                    viewModel.deleteTrack(track.trackId)
                    dialogInterface.dismiss()
                }
            }
        }
    }

    private fun setupMenu() {
        val menuSheet = binding.root.findViewById<View>(R.id.playlists_bottom_sheet)
        val overlay = binding.root.findViewById<View>(R.id.overlay)
        menuBottomSheetBehavior = BottomSheetBehavior.from(menuSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    val isHidden = newState == BottomSheetBehavior.STATE_HIDDEN
                    overlay.visibility = if (isHidden) View.GONE else View.VISIBLE
                    if (isHidden && ::tracksBottomSheetBehavior.isInitialized) {
                        tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    val alpha = ((slideOffset + 1f) / 2f).coerceIn(0f, 1f)
                    overlay.alpha = alpha
                }
            })
        }
        overlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        menuSheet.findViewById<View>(R.id.menuShare).setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }
        menuSheet.findViewById<View>(R.id.menuDelete).setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showOverlayedDialog {
                setTitle(getString(R.string.delete_playlist_title))
                setMessage(getString(R.string.delete_playlist_message))
                setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ -> dialogInterface.dismiss() }
                setPositiveButton(getString(R.string.delete)) { dialogInterface, _ ->
                    viewModel.deletePlaylist {
                        dialogInterface.dismiss()
                        findNavController().navigateUp()
                    }
                }
            }
        }
        menuSheet.findViewById<View>(R.id.menuEdit).setOnClickListener {
            val id = viewModel.state.value.playlist?.id ?: return@setOnClickListener
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val action = R.id.createPlaylistFragment
            val bundle = Bundle().apply { putLong("editPlaylistId", id) }
            findNavController().navigate(action, bundle)
        }
    }

    private fun setupClickListeners() {
        binding.sharePlaylistButton.setOnClickListener { sharePlaylist() }
        binding.menuButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun sharePlaylist() {
        val count = viewModel.state.value.playlist?.tracksCount ?: 0
        val tracksCountText = resources.getQuantityString(R.plurals.tracks_count, count, count)
        val text = viewModel.buildShareText(tracksCountText)
        if (text == null) return
        if (text.isBlank()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.empty_playlist_share_toast),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(sendIntent, getString(R.string.playlist_share))
        val overlay = binding.root.findViewById<View>(R.id.overlay)
        overlay.alpha = 1f
        overlay.visibility = View.VISIBLE
        startActivity(chooser)
    }

    override fun onResume() {
        super.onResume()
        viewModel.reload()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}


