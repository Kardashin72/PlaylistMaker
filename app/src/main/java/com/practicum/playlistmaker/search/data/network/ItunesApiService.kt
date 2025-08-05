package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("/search?entity=song")
    fun searchTracks(@Query("term") expression: String): Call<TracksSearchResponse>
}