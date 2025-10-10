package com.practicum.playlistmaker.medialibrary.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.core.presentation.utils.clickDebounce
import com.practicum.playlistmaker.medialibrary.presentation.ui.adapter.PlaylistsRecyclerViewAdapter
import kotlinx.coroutines.launch

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var adapter: PlaylistsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PlaylistsRecyclerViewAdapter { playlist ->
            val action = com.practicum.playlistmaker.R.id.action_mediaLibraryFragment_to_playlistFragment
            val bundle = Bundle().apply {
                putLong(KEY_PLAYLIST_ID, playlist.id)
            }
            if(clickDebounce()) {
                findNavController().navigate(action, bundle)
            }
        }
        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecyclerView.adapter = adapter
        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(com.practicum.playlistmaker.R.id.createPlaylistFragment)
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { playlists ->
                    binding.playlistsPlaceholder.isVisible = playlists.isEmpty()
                    binding.playlistsRecyclerView.isVisible = playlists.isNotEmpty()
                    if (playlists.isNotEmpty()) {
                        adapter.playlists = playlists
                        adapter.notifyDataSetChanged()
                    } else {
                        adapter.playlists = emptyList()
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val KEY_PLAYLIST_ID = "playlistId"
        fun newInstance() = PlaylistsFragment().apply {
            arguments = Bundle()
        }
    }
}