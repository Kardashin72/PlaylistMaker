package com.practicum.playlistmaker.medialibrary.data

import com.practicum.playlistmaker.medialibrary.data.db.AppDatabase
import com.practicum.playlistmaker.medialibrary.data.db.converters.TracksInPlaylistsDbConverter
import com.practicum.playlistmaker.medialibrary.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn


class PlaylistsRepositoryImpl(
    private val database: AppDatabase
) : PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist): Long {
        return database.playlistsDao().addPlaylist(PlaylistDbConverter.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        database.playlistsDao().updatePlaylist(PlaylistDbConverter.map(playlist))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> =
        database.playlistsDao().getAllPlaylists()
            .map { list -> list.map { PlaylistDbConverter.map(it) } }
            .flowOn(Dispatchers.IO)

    override suspend fun getPlaylistById(id: Long): Playlist? =
        database.playlistsDao().getPlaylistById(id)?.let { PlaylistDbConverter.map(it) }

    override suspend fun addTrackToPlaylist(
        playlist: Playlist,
        track: Track
    ): Boolean {
        val current = database.playlistsDao().getPlaylistById(playlist.id.toLong())
            ?.let { PlaylistDbConverter.map(it) }
            ?: playlist
        if (current.trackIds.contains(track.trackId)) return false
        //был уверен, что добавил эту строку... просто раз пока что нет необходимости читать из этой таблицы - упустил
        database.playlistTracksDao().addTrack(TracksInPlaylistsDbConverter.map(track))
        val updatedIds = (current.trackIds + track.trackId).distinct()
        val updated = current.copy(
            trackIds = updatedIds,
            tracksCount = updatedIds.size
        )
        database.playlistsDao().updatePlaylist(PlaylistDbConverter.map(updated))
        return true
    }
}