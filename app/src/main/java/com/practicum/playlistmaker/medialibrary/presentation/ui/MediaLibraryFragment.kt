package com.practicum.playlistmaker.medialibrary.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentMedialibraryBinding

class MediaLibraryFragment : Fragment() {
    private var _binding: FragmentMedialibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedialibraryBinding.inflate(inflater, container, false)
        return binding.root
    }
}