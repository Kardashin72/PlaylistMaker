package com.practicum.playlistmaker.medialibrary.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.FavouriteTracksViewModel
import com.practicum.playlistmaker.medialibrary.presentation.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment: Fragment() {
    private val favouriteTracksViewModel: FavouriteTracksViewModel by viewModel()
    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val favourites by favouriteTracksViewModel.tracks.observeAsState(emptyList())
                val playlistsState by playlistsViewModel.state.collectAsStateWithLifecycle()
                var selectedTab by remember { mutableStateOf(MediaLibraryTab.FAVOURITES) }

                MediaLibraryScreen(
                    selectedTab = selectedTab,
                    favourites = favourites,
                    playlists = playlistsState,
                    onTabSelected = { selectedTab = it },
                    onFavouriteTrackClick = { track ->
                        val direction = MediaLibraryFragmentDirections.actionMediaLibraryFragmentToPlayerFragment(track)
                        findNavController().navigate(direction)
                    },
                    onPlaylistClick = { playlist ->
                        val action = R.id.action_mediaLibraryFragment_to_playlistFragment
                        val bundle = Bundle().apply {
                            putLong(PlaylistsFragment.KEY_PLAYLIST_ID, playlist.id)
                        }
                        findNavController().navigate(action, bundle)
                    },
                    onCreatePlaylistClick = {
                        findNavController().navigate(com.practicum.playlistmaker.R.id.createPlaylistFragment)
                    }
                )
            }
        }
    }
}