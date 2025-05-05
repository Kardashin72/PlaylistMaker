package com.practicum.playlistmaker.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(val historyPreferences: SharedPreferences) {
    val gson = Gson()
    val type = object : TypeToken<ArrayList<Track>>() {}.type
    fun saveTrackToHistory(track: Track, historyKey: String) {
        val json = historyPreferences.getString(historyKey, null)
        var searchHistory = gson.fromJson<ArrayList<Track>>(json, type) ?: ArrayList()
        val duplicate = searchHistory.find { it.trackId == track.trackId }
        if (duplicate != null) searchHistory.remove(duplicate)
        searchHistory.add(0, track)
        if (searchHistory.size > 10) {
            searchHistory = ArrayList(searchHistory.take(10))
        }
        historyPreferences.edit()
            .putString(historyKey, gson.toJson(searchHistory))
            .apply()
    }

    fun loadSearchHistory(historyKey: String) : ArrayList<Track> {
        val json = historyPreferences.getString(historyKey, null)
        return gson.fromJson(json, type) ?: ArrayList<Track>()
    }

    fun clearSearchHistory() {
        historyPreferences.edit()
            .clear()
            .apply()
    }

    fun hasHistory(historyKey: String): Boolean {
        return !loadSearchHistory(historyKey).isEmpty()
    }
}