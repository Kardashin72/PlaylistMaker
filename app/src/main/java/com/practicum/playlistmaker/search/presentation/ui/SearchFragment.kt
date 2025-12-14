package com.practicum.playlistmaker.search.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.core.presentation.utils.clickDebounce
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.presentation.viewmodel.SearchScreenState
import com.practicum.playlistmaker.search.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.screenState.observeAsState(SearchScreenState())
                var history by remember { mutableStateOf(viewModel.getSearchHistory()) }
                var queryText by remember { mutableStateOf(state.searchQuery) }

                LaunchedEffect(state.searchQuery) {
                    // синхронизация при восстановлении состояния из ViewModel
                    queryText = state.searchQuery
                }

                LaunchedEffect(state.isSearchHistoryVisible) {
                    if (state.isSearchHistoryVisible && viewModel.isHistoryNotEmpty()) {
                        history = viewModel.getSearchHistory()
                    } else if (!state.isSearchHistoryVisible) {
                        history = emptyList()
                    }
                }

                SearchScreen(
                    state = state,
                    history = history,
                    queryText = queryText,
                    onQueryChange = { text ->
                        queryText = text
                        viewModel.updateSearchText(text)
                        if (text.isEmpty()) {
                            searchJob?.cancel()
                        } else {
                            searchDebounce(text)
                        }
                    },
                    onSearchAction = {
                        searchJob?.cancel()
                        if (queryText.isNotBlank()) {
                            viewModel.searchTrack(queryText)
                        }
                    },
                    onClearQueryClick = {
                        searchJob?.cancel()
                        queryText = ""
                        viewModel.clearSearchQuery()
                    },
                    onClearHistoryClick = {
                        viewModel.clearSearchHistory()
                        history = emptyList()
                    },
                    onRefreshClick = {
                        if (queryText.isNotBlank()) {
                            viewModel.searchTrack(queryText)
                        }
                    },
                    onTrackClick = { track ->
                        viewModel.saveTrackToHistory(track)
                        if (clickDebounce()) {
                            showPlayerForTrack(track)
                        }
                    },
                    onHistoryTrackClick = { track ->
                        if (clickDebounce()) {
                            showPlayerForTrack(track)
                        }
                    },
                    onTextFieldFocusChange = { hasFocus ->
                        viewModel.editTextFocusChange(hasFocus)
                    },
                )
            }
        }
    }

    override fun onDestroyView() {
        searchJob?.cancel()
        searchJob = null
        super.onDestroyView()
    }

    private fun searchDebounce(text: String) {
        searchJob?.cancel()
        if (text.isBlank()) return
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            viewModel.searchTrack(text)
        }
    }

    private fun showPlayerForTrack(track: Track) {
        val direction = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
        findNavController().navigate(direction)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}