package com.practicum.playlistmaker.search.data

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?
    fun clearStorage()
    fun isStorageEmpty(): Boolean
}