package com.practicum.playlistmaker.medialibrary.domain.api

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun addPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean
    fun getTracksByIds(ids: List<Int>): Flow<List<Track>>
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Int)
    suspend fun deletePlaylist(playlistId: Long)
}