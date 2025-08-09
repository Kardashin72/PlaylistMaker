package com.practicum.playlistmaker.medialibrary.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding

class MediaLibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarMediaLibrary.setNavigationOnClickListener { finish() }

        binding.viewPagerMediaLibrary.adapter = MediaLibraryPagerAdapter(supportFragmentManager, lifecycle)
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

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}