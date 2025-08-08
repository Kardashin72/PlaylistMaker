package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.domain.api.SearchResult
import com.practicum.playlistmaker.search.domain.api.TracksSearchRepository

class TracksSearchRepositoryImpl(private val networkClient: NetworkClient) :
    TracksSearchRepository {
    override fun searchTracks(expression: String): SearchResult {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return if (response.resultCode == 200) {
            SearchResult.Success((response as TracksSearchResponse).results.map { trackDto ->
                trackDto.toDomain()
            })
        } else {
            SearchResult.ConnectionError
        }
    }
}
