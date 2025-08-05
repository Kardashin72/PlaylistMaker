package com.practicum.playlistmaker.search.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.TracksSearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.search.domain.model.Track

class SearchViewModel(
    val searchHistoryInteractor: TracksSearchHistoryInteractor
) : ViewModel() {
    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> = _tracks

    private val _savedText = MutableLiveData<String>("")
    val savedText: LiveData<String> = _savedText

    private val searchIneractor: TracksSearchInteractor by lazy {
        Creator.provideTracksSearchInteractor()
    }




}