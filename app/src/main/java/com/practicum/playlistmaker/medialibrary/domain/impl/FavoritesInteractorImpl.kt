package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.medialibrary.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.medialibrary.domain.api.FavoritesRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {
    override suspend fun addTrackToFavorites(track: Track) = repository.addTrackToFavorites(track)
    override suspend fun deleteTrackFromFavorites(trackId: Int) = repository.deleteTrackFromFavorites(trackId)
    override fun getFavoriteTracks(): Flow<List<Track>> = repository.getFavoriteTracks()
    override fun getFavoriteTracksId(): Flow<List<Int>> = repository.getFavoriteTracksId()
}