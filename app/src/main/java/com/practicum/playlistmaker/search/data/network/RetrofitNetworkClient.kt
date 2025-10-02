package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest

class RetrofitNetworkClient(private val itunesService: ItunesApiService) : NetworkClient {
    override suspend fun doRequest(dto: Any): Response {
        return if (dto is TracksSearchRequest) {
            try {
                val resp = itunesService.searchTracks(dto.expression)
                resp.apply { resultCode = 200 }
            } catch (e: Exception) {
                Response().apply { resultCode = -1 }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}