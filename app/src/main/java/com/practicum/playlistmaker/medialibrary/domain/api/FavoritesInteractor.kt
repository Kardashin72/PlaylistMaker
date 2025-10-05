package com.practicum.playlistmaker.medialibrary.domain.api

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun deleteTrackFromFavorites(trackId: Int)
    fun getFavoriteTracks(): Flow<List<Track>>
    fun getFavoriteTracksId(): Flow<List<Int>>
}