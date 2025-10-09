package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.medialibrary.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.medialibrary.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository,
) : PlaylistsInteractor {
    override suspend fun addPlaylist(playlist: Playlist): Long {
        return repository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return repository.getPlaylistById(id)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean {
        return repository.addTrackToPlaylist(playlist, track)
    }

    override fun getTracksByIds(ids: List<Int>) = repository.getTracksByIds(ids)

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Int) {
        repository.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }
}