package com.practicum.playlistmaker.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TracksSearchApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackSearchResponse>
}