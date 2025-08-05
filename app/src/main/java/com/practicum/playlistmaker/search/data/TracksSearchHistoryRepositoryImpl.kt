package com.practicum.playlistmaker.search.data

import android.content.SharedPreferences
import com.bumptech.glide.load.engine.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.api.TracksSearchHistoryRepository
import com.practicum.playlistmaker.search.domain.model.Track

class TracksSearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>
) : TracksSearchHistoryRepository {


    override fun saveTrackToHistory(track: Track) {
        var tracks = storage.getData() ?: arrayListOf()
        if (!tracks.contains(track)) {
                tracks.add(0, track)
        }
        if (tracks.size > 10) {
            tracks = ArrayList(tracks.take(10))
        }
        storage.storeData(tracks)
    }

    override fun loadSearchHistory(): List<Track> {
        return storage.getData() ?: listOf()
    }

    override fun clearSearchHistory() {
        storage.clearStorage()
    }

    override fun hasHistory(): Boolean {
        return !storage.isStorageEmpty()
    }
}
