package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksSearchRequest

class RetrofitNetworkClient(private val itunesService: ItunesApiService) : NetworkClient {
   override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            return try {
                val resp = itunesService.searchTracks(dto.expression).execute()
                val body = resp.body() ?: Response()
                body.apply { resultCode = resp.code() }
            } catch (e: Exception) {
                Response().apply { resultCode = -1 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}