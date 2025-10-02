package com.practicum.playlistmaker.search.presentation.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.presentation.viewmodel.SearchScreenState
import com.practicum.playlistmaker.search.presentation.viewmodel.SearchViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList
import java.util.Collections.emptyList

class SearchFragment: Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchAdapter: SearchRecycleViewAdapter
    private lateinit var searchHistoryAdapter: SearchRecycleViewAdapter
    private var isClickAllowed = true
    private var searchJob: Job? = null
    private var clickJob: Job? = null
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupRecyclerViews()
        setupTextWatcher()
        observeViewModel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun updateUI(state: SearchScreenState) {
        when (state.screenStatus) {
            is SearchScreenState.ScreenStatus.Default -> {
                binding.apply {
                    progressBar.isVisible = false
                    searchRecycleView.isVisible = false
                    notFoundErrorMessage.isVisible = false
                    connectionErrorMessage.isVisible = false
                }
            }
            is SearchScreenState.ScreenStatus.Loading -> {
                binding.apply {
                    progressBar.isVisible = true
                    searchRecycleView.isVisible = false
                    notFoundErrorMessage.isVisible = false
                    connectionErrorMessage.isVisible = false
                }
            }
            is SearchScreenState.ScreenStatus.LoadSuccess -> {
                binding.apply {
                    progressBar.isVisible = false
                    searchRecycleView.isVisible = true
                    notFoundErrorMessage.isVisible = false
                    connectionErrorMessage.isVisible = false
                }
                searchAdapter.tracks = ArrayList(state.tracks)
                searchAdapter.notifyDataSetChanged()
            }
            is SearchScreenState.ScreenStatus.NotFoundError -> {
                binding.apply {
                    progressBar.isVisible = false
                    searchRecycleView.isVisible = false
                    notFoundErrorMessage.isVisible = true
                    connectionErrorMessage.isVisible = false
                }
            }
            is SearchScreenState.ScreenStatus.ConnectionError -> {
                binding.apply {
                    progressBar.isVisible = false
                    searchRecycleView.isVisible = false
                    notFoundErrorMessage.isVisible = false
                    connectionErrorMessage.isVisible = true
                }
            }
        }
        updateSearchHistoryVisibility(state.isSearchHistoryVisible)
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }

    private fun setupClickListeners() {
        //обработка нажатия на кнопку "ОК" экранной клавиатуры
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }

        //обработка нажатия на кнопку "Обновить" в случае отсутствия интернета
        binding.searchRefreshButton.setOnClickListener {
            searchTrack()
        }

        //обработка нажатия на кнопку очистки строки ввода
        binding.clearSearchTextButton.setOnClickListener {
            binding.searchEditText.text.clear()
            viewModel.clearSearchQuery()
            hideKeyboard(binding.searchEditText)
        }

        //обработка нажатия на кнопку очистки истории поиска
        binding.clearSearchHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
            updateSearchHistory()
        }

        //обработка изменения состояния фокуса поля ввода текста
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.editTextFocusChange(hasFocus)
        }
    }

    private fun setupRecyclerViews() {
        //настройка адаптера и layoutManager для результатов поиска
        searchAdapter = SearchRecycleViewAdapter(viewModel.screenState.value?.tracks ?: emptyList()) { track ->
            viewModel.saveTrackToHistory(track)
            updateSearchHistory()
            if (clickDebounce()) {
                showPlayerForTrack(track)
            }
        }
        binding.searchRecycleView.adapter = searchAdapter
        binding.searchRecycleView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        //настройка адаптера и layoutManager для истории поиска
        searchHistoryAdapter = SearchRecycleViewAdapter(ArrayList()) { track ->
            if (clickDebounce()) {
                showPlayerForTrack(track)
            }
        }
        binding.searchHistoryRecyclerView.adapter = searchHistoryAdapter
        binding.searchHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun setupTextWatcher() {
        //установка TextWatcher на EditText
        binding.searchEditText.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                binding.clearSearchTextButton.isVisible = !s.isNullOrEmpty()
                searchDebounce()
            },
            afterTextChanged = { s ->
                val text = s?.toString() ?: ""
                viewModel.updateSearchText(text)
                if (text.isEmpty()) {
                    viewModel.clearSearchQuery()
                }
            }
        )
    }

    private fun updateSearchHistory() {
        searchHistoryAdapter.tracks = viewModel.getSearchHistory()
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun updateSearchHistoryVisibility(isVisible: Boolean) {
        if (isVisible && viewModel.isHistoryNotEmpty()) {
            binding.historyView.isVisible = true
            updateSearchHistory()
        } else {
            binding.historyView.isVisible = false
        }
    }

    //функция, отвечающая за отключение клавиатуры при нажатии на кнопку очистки строки ввода
    private fun hideKeyboard(view: View) {
        val inputManager =
            view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    //функция поиска трека через viewModel
    private fun searchTrack() {
        val query = binding.searchEditText.text.toString()
        viewModel.searchTrack(query)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickJob?.cancel()
            clickJob = lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun searchDebounce() {
        val text = binding.searchEditText.text
        searchJob?.cancel()
        if (text.isNullOrEmpty()) return
        searchJob = lifecycleScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTrack()
        }
    }

    private fun showPlayerForTrack(track: Track) {
        val direction = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
        findNavController().navigate(direction)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}