package com.practicum.playlistmaker.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.medialibrary.data.db.entity.TracksInPlaylistsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksInPLaylistsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrack(track: TracksInPlaylistsEntity)

    @Query("SELECT * FROM playlist_tracks ORDER BY addedTime DESC")
    fun getAllTracks(): Flow<List<TracksInPlaylistsEntity>>

    @Query("SELECT * FROM playlist_tracks ORDER BY addedTime DESC")
    suspend fun getAllTracksOnce(): List<TracksInPlaylistsEntity>

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Int)
}