package com.practicum.playlistmaker.search.presentation.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.api.SearchResult
import com.practicum.playlistmaker.search.domain.api.TracksSearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.search.domain.model.Track

data class SearchScreenState(
    val tracks: List<Track> = emptyList(),
    val searchQuery: String = "",
    val screenStatus: ScreenStatus = ScreenStatus.Default,
    val isSearchHistoryVisible: Boolean = false,
) {
    sealed class ScreenStatus {
        object Default : ScreenStatus()
        object Loading : ScreenStatus()
        object LoadSuccess : ScreenStatus()
        object NotFoundError : ScreenStatus()
        object ConnectionError : ScreenStatus()
    }
}

    class SearchViewModel(
        val searchHistoryInteractor: TracksSearchHistoryInteractor,
        private val searchInteractor: TracksSearchInteractor,
    ) : ViewModel() {
        //LiveData для сохранения состояния экрана
        private val _screenState = MutableLiveData<SearchScreenState>(SearchScreenState())
        val screenState: LiveData<SearchScreenState> = _screenState

        private var currentSearchText = ""
        private var hasFocus = false

        fun searchTrack(query: String) {
            currentSearchText = query
            if (query.isBlank()) {
                _screenState.postValue(SearchScreenState(
                    searchQuery = query,
                    screenStatus = SearchScreenState.ScreenStatus.Default
                ))
                return
            }
            _screenState.postValue(SearchScreenState(
                searchQuery = query,
                screenStatus = SearchScreenState.ScreenStatus.Loading
            ))
            searchInteractor.searchTracks(query, object : TracksSearchInteractor.TracksConsumer {
                override fun consume(result: SearchResult) {
                    when (result) {
                        is SearchResult.Success -> {
                            val newState = if (result.tracks.isNotEmpty()) {
                                SearchScreenState(
                                    tracks = result.tracks,
                                    searchQuery = query,
                                    screenStatus = SearchScreenState.ScreenStatus.LoadSuccess
                                )
                            } else {
                                SearchScreenState(
                                    searchQuery = query,
                                    screenStatus = SearchScreenState.ScreenStatus.NotFoundError
                                )
                            }
                            _screenState.postValue(newState)
                        }
                        is SearchResult.ConnectionError -> {
                            _screenState.postValue(
                                SearchScreenState(
                                searchQuery = query,
                                screenStatus = SearchScreenState.ScreenStatus.ConnectionError
                                ))
                        }
                    }
                }
            })
        }

        fun clearSearchQuery() {
            currentSearchText = ""
            _screenState.postValue(SearchScreenState())
        }

        fun updateSearchText(text: String) {
            currentSearchText = text
            searchHistoryVisibilityControl()
        }

        fun saveTrackToHistory(track: Track) {
            searchHistoryInteractor.saveTrackToHistory(track)
        }

        fun getSearchHistory(): List<Track> {
            return searchHistoryInteractor.loadSearchHistory()
        }

        fun clearSearchHistory() {
            searchHistoryInteractor.clearSearchHistory()
            _screenState.postValue(_screenState.value?.copy(
                isSearchHistoryVisible = false
            ))
        }

        fun isHistoryNotEmpty(): Boolean {
            return searchHistoryInteractor.hasHistory()
        }

        // методы для сохранения/восстановления активити
        fun saveState(): Bundle {
            val currentState = _screenState.value ?: SearchScreenState()
            return Bundle().apply {
                putString("search_query", currentState.searchQuery)
                putString("screen_status", currentState.screenStatus.javaClass.simpleName)
                putBoolean(KEY_SEARCH_HISTORY_VISIBLE, currentState.isSearchHistoryVisible)
                // Сохраняем треки только если они есть
                if (currentState.tracks.isNotEmpty()) {
                    putParcelableArrayList("tracks", ArrayList(currentState.tracks))
                }
            }
        }

        fun restoreState(savedState: Bundle?) {
            savedState?.let { bundle ->
                val searchQuery = bundle.getString(KEY_SEARCH_QUERY, "")
                val statusName = bundle.getString(KEY_SCREEN_STATUS, "Default")
                @Suppress("DEPRECATION")
                val tracks = bundle.getParcelableArrayList<Track>(KEY_TRACKS) ?: emptyList()
                val isSearchHistoryVisible = bundle.getBoolean(KEY_SEARCH_HISTORY_VISIBLE, false)

                // Восстанавливаем правильное состояние экрана
                val screenStatus = when (statusName) {
                    STATUS_DEFAULT -> SearchScreenState.ScreenStatus.Default
                    STATUS_LOAD_SUCCESS -> SearchScreenState.ScreenStatus.LoadSuccess
                    STATUS_LOADING -> SearchScreenState.ScreenStatus.Loading
                    STATUS_NOT_FOUND_ERROR -> SearchScreenState.ScreenStatus.NotFoundError
                    STATUS_CONNECTION_ERROR -> SearchScreenState.ScreenStatus.ConnectionError
                    else -> SearchScreenState.ScreenStatus.Default
                }

                _screenState.postValue(SearchScreenState(
                    tracks = tracks,
                    searchQuery = searchQuery,
                    screenStatus = screenStatus,
                    isSearchHistoryVisible = isSearchHistoryVisible
                ))
            }
        }

        private fun showSearchHistory() {
            _screenState.postValue(SearchScreenState(
                isSearchHistoryVisible = true
            ))
        }

        private fun hideSearchHistory() {
            _screenState.postValue(SearchScreenState(
                isSearchHistoryVisible = false
            ))
        }

        private fun searchHistoryVisibilityControl() {
            if (hasFocus && currentSearchText.isEmpty() && isHistoryNotEmpty()) {
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }

        fun editTextFocusChange(hasFocus: Boolean) {
            this.hasFocus = hasFocus
            searchHistoryVisibilityControl()
        }

        companion object {
            // Константы для ключей сохранения состояния
            private const val KEY_SEARCH_QUERY = "SEARCH_QUERY"
            private const val KEY_SCREEN_STATUS = "SCREEN_STATUS"
            private const val KEY_TRACKS = "TRACKS"
            private const val KEY_SEARCH_HISTORY_VISIBLE = "SEARCH_HISTORY_VISIBLE"

            // Константы для статусов экрана
            private const val STATUS_LOAD_SUCCESS = "LoadSuccess"
            private const val STATUS_LOADING = "Loading"
            private const val STATUS_NOT_FOUND_ERROR = "NotFoundError"
            private const val STATUS_CONNECTION_ERROR = "ConnectionError"
            private const val STATUS_DEFAULT = "Default"
        }
    }