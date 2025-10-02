package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.domain.api.SearchResult
import com.practicum.playlistmaker.search.domain.api.TracksSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TracksSearchRepositoryImpl(private val networkClient: NetworkClient) :
    TracksSearchRepository {
    override fun searchTracks(expression: String): Flow<SearchResult> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        val result = if (response.resultCode == 200) {
            SearchResult.Success((response as TracksSearchResponse).results.map { trackDto ->
                trackDto.toDomain()
            })
        } else {
            SearchResult.ConnectionError
        }
        emit(result)
    }.flowOn(Dispatchers.IO)
}
