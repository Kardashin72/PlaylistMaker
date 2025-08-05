package com.practicum.playlistmaker.search.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.StorageClient
import java.lang.reflect.Type

class PrefsStorageClient<T> (
    private val context: Context,
    private val dataKey: String,
    private val type: Type
) : StorageClient<T> {
    private val prefs: SharedPreferences = context.getSharedPreferences(HISTORY_PREFERENCES_KEY, Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun storeData(data: T) {
        prefs.edit().putString(dataKey, gson.toJson(data, type)).apply()
    }

    override fun getData(): T? {
        val dataJson = prefs.getString(dataKey, null)
        if (dataJson == null) {
            return null
        } else {
            return gson.fromJson(dataJson, type)
        }
    }

    override fun clearStorage() {
        prefs.edit().clear().apply()
    }

    override fun isStorageEmpty(): Boolean {
        val dataJson = prefs.getString(dataKey, null)
        return dataJson == null
    }

    companion object {
        const val HISTORY_PREFERENCES_KEY = "HISTORY_PREFERENCES_KEY"
    }

}