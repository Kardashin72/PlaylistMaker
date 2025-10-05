package com.practicum.playlistmaker.medialibrary.data

import com.practicum.playlistmaker.medialibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.model.Track

object TrackDbConverter {
    fun map(track: Track): TrackEntity = TrackEntity(
        trackId = track.trackId,
        trackName = track.trackName,
        artistName = track.artistName,
        trackTime = track.trackTime,
        artworkUrl100 = track.artworkUrl100,
        collectionName = track.collectionName,
        releaseDate = track.releaseDate,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
        previewUrl = track.previewUrl,
        addedTime = System.currentTimeMillis()
    )

    fun map(entity: TrackEntity): Track = Track(
        trackId = entity.trackId,
        trackName = entity.trackName,
        artistName = entity.artistName,
        trackTime = entity.trackTime,
        artworkUrl100 = entity.artworkUrl100,
        collectionName = entity.collectionName,
        releaseDate = entity.releaseDate,
        primaryGenreName = entity.primaryGenreName,
        country = entity.country,
        previewUrl = entity.previewUrl
    )
}