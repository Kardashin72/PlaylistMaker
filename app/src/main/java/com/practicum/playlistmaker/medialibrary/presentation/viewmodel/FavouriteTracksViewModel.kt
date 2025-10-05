package com.practicum.playlistmaker.medialibrary.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(
    private val favoritesInteractor: FavoritesInteractor
): ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> = _tracks

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks().collect { list ->
                _tracks.postValue(list)
            }
        }
    }
}