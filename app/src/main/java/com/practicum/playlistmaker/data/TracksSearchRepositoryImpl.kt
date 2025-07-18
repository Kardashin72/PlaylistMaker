package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.api.SearchResult
import com.practicum.playlistmaker.domain.api.TracksSearchRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksSearchRepositoryImpl(private val networkClient: NetworkClient) : TracksSearchRepository {
    override fun searchTracks(expression: String): SearchResult {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return if (response.resultCode == 200) {
            SearchResult.Success((response as TracksSearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    trackTimeConvert(it.trackTime),
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            })
        } else {
            SearchResult.ConnectionError
        }
    }
}

private fun trackTimeConvert(ms: Long): String {
    val minutes = ms / 1000 / 60
    val seconds = ms / 1000 % 60
    return String.format("%02d:%02d", minutes, seconds)
}