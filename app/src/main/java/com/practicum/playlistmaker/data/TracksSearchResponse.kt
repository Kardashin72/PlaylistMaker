package com.practicum.playlistmaker.data

import com.google.gson.annotations.SerializedName

class TrackSearchResponse(@SerializedName("results") val dtoTracks: List<DtoTrack>)

data class DtoTrack(
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Long,
    val artworkUrl100: String
)