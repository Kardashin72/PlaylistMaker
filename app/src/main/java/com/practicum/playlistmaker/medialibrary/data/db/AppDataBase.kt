package com.practicum.playlistmaker.medialibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.practicum.playlistmaker.medialibrary.data.db.dao.FavoritesDao
import com.practicum.playlistmaker.medialibrary.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.medialibrary.data.db.dao.TracksInPLaylistsDao
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.medialibrary.data.db.entity.TracksInPlaylistsEntity

@Database(
    entities = [TrackEntity::class, PlaylistEntity::class, TracksInPlaylistsEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun playlistTracksDao(): TracksInPLaylistsDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS playlists (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "name TEXT NOT NULL, " +
                            "description TEXT NOT NULL, " +
                            "coverImagePath TEXT, " +
                            "trackIdsJson TEXT NOT NULL, " +
                            "tracksCount INTEGER NOT NULL"
                            + ")"
                )
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS playlist_tracks (" +
                            "trackId INTEGER NOT NULL PRIMARY KEY, " +
                            "trackName TEXT NOT NULL, " +
                            "artistName TEXT NOT NULL, " +
                            "trackTime TEXT NOT NULL, " +
                            "artworkUrl100 TEXT NOT NULL, " +
                            "collectionName TEXT NOT NULL, " +
                            "releaseDate TEXT NOT NULL, " +
                            "primaryGenreName TEXT NOT NULL, " +
                            "country TEXT NOT NULL, " +
                            "previewUrl TEXT NOT NULL, " +
                            "addedTime INTEGER NOT NULL DEFAULT 0"
                            + ")"
                )
            }
        }
    }
}