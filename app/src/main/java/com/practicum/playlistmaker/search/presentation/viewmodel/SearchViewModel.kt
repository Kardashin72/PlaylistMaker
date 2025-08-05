package com.practicum.playlistmaker.search.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.TracksSearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.search.domain.model.Track

data class SearchScreenState(
    val tracks: List<Track> = emptyList<Track>(),
    val savedText: String = "",
    val screenStatus: ScreenStatus = ScreenStatus.Default,
) {
    sealed class ScreenStatus {
        object Default : ScreenStatus()
        object Loading : ScreenStatus()
        object LoadSuccess : ScreenStatus()
        object notFoundError : ScreenStatus()
        object connectionError : ScreenStatus()
    }
}

    class SearchViewModel(
        val searchHistoryInteractor: TracksSearchHistoryInteractor,
    ) : ViewModel() {
        private val _screenState = MutableLiveData<SearchScreenState>(SearchScreenState())
        val screenState: LiveData<SearchScreenState> = _screenState

        private val searchIneractor: TracksSearchInteractor by lazy {
            Creator.provideTracksSearchInteractor()
        }

        fun searchTrack(query: String) {
            _screenState.postValue(SearchScreenState(emptyList(), query, SearchScreenState.ScreenStatus.Loading))
            searchIneractor.searchTracks(query)
        }

        companion object {
            fun getFactory(searchHistoryInteractor: TracksSearchHistoryInteractor): ViewModelProvider.Factory =
                viewModelFactory {
                    initializer {
                        SearchViewModel(searchHistoryInteractor)
                    }
                }
        }
    }