package com.practicum.playlistmaker.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.practicum.playlistmaker.medialibrary.data.db.entity.TracksInPlaylistsEntity

@Dao
interface TracksInPLaylistsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrack(track: TracksInPlaylistsEntity)
}