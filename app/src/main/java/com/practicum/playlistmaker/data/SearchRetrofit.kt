package com.practicum.playlistmaker.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//настройка Retrofit
private val searchBaseUrl = "https://itunes.apple.com"

private val retrofit = Retrofit.Builder()
    .baseUrl(searchBaseUrl)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val searchApiService = retrofit.create(TracksSearchApi::class.java)