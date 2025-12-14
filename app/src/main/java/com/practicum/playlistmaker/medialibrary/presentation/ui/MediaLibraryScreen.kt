package com.practicum.playlistmaker.medialibrary.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.google.android.material.R as MaterialR
import com.practicum.playlistmaker.core.presentation.ui.themeColor
import com.practicum.playlistmaker.core.presentation.ui.components.TrackItem
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

enum class MediaLibraryTab {
    FAVOURITES, PLAYLISTS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaLibraryScreen(
    selectedTab: MediaLibraryTab,
    favourites: List<Track>,
    playlists: List<Playlist>,
    onTabSelected: (MediaLibraryTab) -> Unit,
    onFavouriteTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = selectedTab.ordinal,
        pageCount = { 2 },
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        onTabSelected(MediaLibraryTab.values()[pagerState.currentPage])
    }
    Scaffold(
        containerColor = themeColor(attrRes = MaterialR.attr.colorPrimary),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.text_media_library),
                        color = themeColor(attrRes = MaterialR.attr.colorOnPrimary),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeColor(attrRes = MaterialR.attr.colorPrimary),
                ),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val tabs = listOf(
                stringResource(id = R.string.favourite_tracks),
                stringResource(id = R.string.playlists)
            )

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = themeColor(attrRes = MaterialR.attr.colorPrimary),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .padding(horizontal = 16.dp)
                            .height(2.dp),
                        color = themeColor(attrRes = MaterialR.attr.colorOnPrimary),
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                color = themeColor(attrRes = MaterialR.attr.colorOnPrimary),
                            )
                        },
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    when (page) {
                        0 -> FavouritesTabContent(
                            favourites = favourites,
                            onTrackClick = onFavouriteTrackClick,
                        )
                        1 -> PlaylistsTabContent(
                            playlists = playlists,
                            onPlaylistClick = onPlaylistClick,
                            onCreatePlaylistClick = onCreatePlaylistClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavouritesTabContent(
    favourites: List<Track>,
    onTrackClick: (Track) -> Unit,
) {
    if (favourites.isEmpty()) {
        Placeholder(
            text = stringResource(id = R.string.favourite_tracks_placeholder_text)
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(favourites, key = { it.trackId }) { track ->
                TrackItem(
                    track = track,
                    onClick = { onTrackClick(track) },
                )
            }
        }
    }
}

@Composable
private fun PlaylistsTabContent(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = onCreatePlaylistClick,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = themeColor(attrRes = MaterialR.attr.colorSecondary),
                contentColor = themeColor(attrRes = MaterialR.attr.colorOnSecondary),
            ),
        ) {
            Text(text = stringResource(id = R.string.new_playlist))
        }

        if (playlists.isEmpty()) {
            Spacer(modifier = Modifier.height(40.dp))
            Placeholder(
                text = stringResource(id = R.string.playlists_placeholder_text)
            )
        } else {
            Spacer(modifier = Modifier.height(24.dp))
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(playlists, key = { it.id }) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.placeholder),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = playlist.name,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = pluralStringResource(
                id = R.plurals.tracks_count,
                count = playlist.tracksCount,
                playlist.tracksCount
            ),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun Placeholder(
    text: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(106.dp))
        Image(
            painter = painterResource(id = R.drawable.not_found_image),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
    }
}


