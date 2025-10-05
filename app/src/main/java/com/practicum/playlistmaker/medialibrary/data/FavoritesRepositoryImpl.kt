package com.practicum.playlistmaker.medialibrary.data

import com.practicum.playlistmaker.medialibrary.data.db.AppDatabase
import com.practicum.playlistmaker.medialibrary.domain.api.FavoritesRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val database: AppDatabase
) : FavoritesRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        database.favoritesDao().addTrackToFavorites(TrackDbConverter.map(track))
    }

    override suspend fun deleteTrackFromFavorites(trackId: Int) {
        database.favoritesDao().deleteTrackFromFavorites(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> =
        database.favoritesDao().getFavoriteTracks()
            .map { entities -> entities.map { TrackDbConverter.map(it) } }
            .flowOn(Dispatchers.IO)

    override fun getFavoriteTracksId(): Flow<List<Int>> =
        database.favoritesDao().getFavoriteTracksId()
            .flowOn(Dispatchers.IO)
}