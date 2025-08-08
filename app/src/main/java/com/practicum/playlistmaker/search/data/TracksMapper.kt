package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.core.presentation.utils.trackTimeConvert
import com.practicum.playlistmaker.search.domain.model.Track

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
