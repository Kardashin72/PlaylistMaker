package com.practicum.playlistmaker.core.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityRootBinding


class RootActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.isVisible = destination.id != R.id.playerFragment && destination.id != R.id.createPlaylistFragment && destination.id != R.id.playlistFragment
            binding.bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val currentDestinationId = navController.currentDestination?.id

            if (currentDestinationId == item.itemId) return@setOnItemSelectedListener true

            val currentIndex = when (currentDestinationId) {
                R.id.searchFragment -> 0
                R.id.mediaLibraryFragment -> 1
                R.id.settingsFragment ->2
                else -> 0
            }

            val targetIndex = when (item.itemId) {
                R.id.searchFragment -> 0
                R.id.mediaLibraryFragment -> 1
                R.id.settingsFragment ->2
                else -> 0
            }

            val isMovingRight = currentIndex < targetIndex

            val animationOptions = if (isMovingRight) {
                NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .setLaunchSingleTop(true)
                    .build()
            } else {
                NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_left)
                    .setExitAnim(R.anim.slide_out_right)
                    .setPopEnterAnim(R.anim.slide_in_right)
                    .setPopExitAnim(R.anim.slide_out_left)
                    .setLaunchSingleTop(true)
                    .build()
            }

            if (item.itemId == R.id.mediaLibraryFragment) {
                navController.popBackStack(R.id.mediaLibraryFragment, false)
                return@setOnItemSelectedListener true
            }

            navController.popBackStack(R.id.mediaLibraryFragment, false)
            navController.navigate(item.itemId, null, animationOptions)
            return@setOnItemSelectedListener true
        }
    }
}

