package com.practicum.playlistmaker.data

import com.google.gson.annotations.SerializedName

class TrackSearchResponse(@SerializedName("results") val dtoTracks: List<DtoTrack>)

data class DtoTrack(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
)