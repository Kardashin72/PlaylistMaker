package com.practicum.playlistmaker.medialibrary.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.medialibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

object PlaylistDbConverter {
    private val gson = Gson()

    fun map(playlist: Playlist): PlaylistEntity {
        val idsJson = gson.toJson(playlist.trackIds)
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverImagePath = playlist.coverImagePath,
            trackIdsJson = idsJson,
            tracksCount = playlist.tracksCount,
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Int>>() {}.type
        val ids: List<Int> = gson.fromJson(entity.trackIdsJson, type) ?: emptyList()
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverImagePath = entity.coverImagePath,
            trackIds = ids,
            tracksCount = entity.tracksCount,
        )
    }
}