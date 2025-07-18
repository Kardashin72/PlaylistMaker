package com.practicum.playlistmaker.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.api.TracksSearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksSearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson = Gson()
) : TracksSearchHistoryRepository  {
    private val type = object : TypeToken<ArrayList<TrackDto>>() {}.type

    override fun saveTrackToHistory(track: Track) {
        val json = sharedPreferences.getString(HISTORY_PREFERENCES_KEY, null)
        var searchHistory = gson.fromJson<ArrayList<TrackDto>>(json, type) ?: ArrayList()
        val duplicate = searchHistory.find { it.trackId == track.trackId }
        if (duplicate != null) searchHistory.remove(duplicate)
        searchHistory.add(0, track.toDto())
        if (searchHistory.size > 10) {
            searchHistory = ArrayList(searchHistory.take(10))
        }
        sharedPreferences.edit()
            .putString(HISTORY_PREFERENCES_KEY, gson.toJson(searchHistory))
            .apply()
    }

    override fun loadSearchHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_PREFERENCES_KEY, null)
        val dtos = gson.fromJson<ArrayList<TrackDto>>(json, type) ?: ArrayList()
        return dtos.map { it.toDomain() }
    }

    override fun clearSearchHistory() {
        sharedPreferences.edit().clear().apply()
    }

    override fun hasHistory(): Boolean {
        return loadSearchHistory().isNotEmpty()
    }

    companion object {
        const val HISTORY_PREFERENCES_KEY = "HISTORY_PREFERENCES_KEY"
    }
}
