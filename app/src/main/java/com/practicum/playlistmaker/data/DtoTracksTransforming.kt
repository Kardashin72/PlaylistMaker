package com.practicum.playlistmaker.data


private fun trackTimeConvert(ms: Long) : String {
    val minutes = ms/1000/60
    val seconds = ms/1000%60
    return String.format("%02d:%02d", minutes, seconds)
}

private fun dtoTrackToTrack(dto: DtoTrack) : Track {
    return Track(
        trackName = dto.trackName,
        artistName = dto.artistName,
        trackTime = trackTimeConvert(dto.trackTime),
        artworkUrl100 = dto.artworkUrl100
    )
}

fun dtoTracksToTrackList(response: TrackSearchResponse?) : List<Track> {
    val trackList = mutableListOf<Track>()
    for (dtoTrack in response!!.dtoTracks) {
        trackList.add(dtoTrackToTrack(dtoTrack))
    }
    return trackList
}

