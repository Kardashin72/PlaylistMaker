package com.practicum.playlistmaker.medialibrary.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.FavouriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.view.isVisible
import com.practicum.playlistmaker.search.presentation.ui.SearchRecycleViewAdapter
import androidx.navigation.fragment.findNavController
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.presentation.utils.clickDebounce
import com.practicum.playlistmaker.search.domain.model.Track

class FavouriteTracksFragment() : Fragment() {
    private var _binding: FragmentFavouriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavouriteTracksViewModel by viewModel()
    private lateinit var favoritesAdapter: SearchRecycleViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        favoritesAdapter = SearchRecycleViewAdapter(emptyList()) { track ->
            if (clickDebounce()) {
                showPlayerForTrack(track)
            }
        }
        binding.favoriteTracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoriteTracksRecyclerView.adapter = favoritesAdapter
    }

    private fun observeViewModel() {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            val hasData = tracks.isNotEmpty()
            binding.favoriteTracksPlaceholder.isVisible = !hasData
            binding.favoriteTracksRecyclerView.isVisible = hasData
            favoritesAdapter.tracks = ArrayList(tracks)
            favoritesAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun showPlayerForTrack(track: Track) {
        val direction = MediaLibraryFragmentDirections.actionMediaLibraryFragmentToPlayerFragment(track)
        findNavController().navigate(direction)
    }

    companion object {
        fun newInstance(): FavouriteTracksFragment = FavouriteTracksFragment().apply {
            arguments = Bundle()
        }
    }
}