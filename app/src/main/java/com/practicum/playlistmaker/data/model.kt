package com.practicum.playlistmaker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track (
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
): Parcelable


