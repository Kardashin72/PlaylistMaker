package com.practicum.playlistmaker.medialibrary.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaLibraryBinding

class MediaLibraryFragment: Fragment() {
    private var _binding: FragmentMediaLibraryBinding? = null
    private val binding get() = _binding!!
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPagerMediaLibrary.adapter = MediaLibraryPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPagerMediaLibrary.offscreenPageLimit = 2

        tabMediator = TabLayoutMediator(binding.tabLayoutMediaLibrary, binding.viewPagerMediaLibrary) { tab, position ->
            tab.text = when(position) {
                0 -> getString(R.string.favourite_tracks)
                1 -> getString(R.string.playlists)
                else -> ""
            }
        }
        tabMediator.attach()
    }

    override fun onDestroyView() {
        tabMediator.detach()
        _binding = null
        super.onDestroyView()
    }
}