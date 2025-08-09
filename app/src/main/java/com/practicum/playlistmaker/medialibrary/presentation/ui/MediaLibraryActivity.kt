package com.practicum.playlistmaker.medialibrary.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding

class MediaLibraryActivity : AppCompatActivity() {
    private var _binding: ActivityMediaLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarMediaLibrary.setNavigationOnClickListener { finish() }

        binding.viewPagerMediaLibrary.adapter = MediaLibraryPagerAdapter(this)
        binding.viewPagerMediaLibrary.offscreenPageLimit = 2

        TabLayoutMediator(binding.tabLayoutMediaLibrary, binding.viewPagerMediaLibrary) { tab, position ->
            tab.text = when(position) {
                0 -> "Избранные треки"
                1 -> "Плейлисты"
                else -> ""
            }
        }.attach()
    }
}