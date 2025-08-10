package com.practicum.playlistmaker.medialibrary.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentMedialibraryBinding
import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.MediaLibraryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment() : Fragment() {
    private lateinit var binding: FragmentMedialibraryBinding
    private val viewModel: MediaLibraryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMedialibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(): MediaLibraryFragment = MediaLibraryFragment().apply {
            arguments = Bundle()
        }
    }
}