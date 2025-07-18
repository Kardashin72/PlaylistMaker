package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.utils.trackTimeConvert
import com.practicum.playlistmaker.domain.models.Track

fun Track.toDto(): TrackDto = TrackDto(
    trackId = this.trackId,
    trackName = this.trackName,
    artistName = this.artistName,
    trackTime = this.trackTime.toLongOrNull() ?: 0L,
    artworkUrl100 = this.artworkUrl100,
    collectionName = this.collectionName,
    releaseDate = this.releaseDate,
    primaryGenreName = this.primaryGenreName,
    country = this.country,
    previewUrl = this.previewUrl
)

fun TrackDto.toDomain(): Track = Track(
    trackId = this.trackId,
    trackName = this.trackName,
    artistName = this.artistName,
    trackTime = trackTimeConvert(this.trackTime),
    artworkUrl100 = this.artworkUrl100,
    collectionName = this.collectionName,
    releaseDate = this.releaseDate,
    primaryGenreName = this.primaryGenreName,
    country = this.country,
    previewUrl = this.previewUrl
)
