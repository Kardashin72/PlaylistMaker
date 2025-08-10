package com.practicum.playlistmaker.search.presentation.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.presentation.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.presentation.viewmodel.SearchScreenState
import com.practicum.playlistmaker.search.presentation.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment: Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: SearchRecycleViewAdapter
    private lateinit var searchHistoryAdapter: SearchRecycleViewAdapter
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTrack() }
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupRecyclerViews()
        setupTextWatcher()
        observeViewModel()
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
        viewModel.screenState.observe() { state ->
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

        //обработка нажатия на кнопку "Назад"
        binding.toolbarSearch.setNavigationOnClickListener {
            findNavController().navigateUp()
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
                findNavController().navigate(
                    R.id.action_searchFragment_to_playerFragment,
                    PlayerFragment.createArgs(track)
                )
            }
        }
        binding.searchRecycleView.adapter = searchAdapter
        binding.searchRecycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //настройка адаптера и layoutManager для истории поиска
        searchHistoryAdapter = SearchRecycleViewAdapter(ArrayList()) { track ->
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }
        binding.searchHistoryRecyclerView.adapter = searchHistoryAdapter
        binding.searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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

    //сохранение текста из строки ввода
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(KEY_VIEW_MODEL_STATE, viewModel.saveState())
        outState.putInt(CURSOR_POSITION, binding.searchEditText.selectionStart)
    }

    //восстановление строки ввода из сохраненного Bundle
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val viewModelState = savedInstanceState.getBundle(KEY_VIEW_MODEL_STATE)
        viewModel.restoreState(viewModelState)
        val cursorPosition = savedInstanceState.getInt(CURSOR_POSITION, 0)
        binding.searchEditText.setSelection(cursorPosition.coerceIn(0, binding.searchEditText.text.length))
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
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        if (binding.searchEditText.text.isNullOrEmpty()) {
            return
        }
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        private const val KEY_VIEW_MODEL_STATE = "VIEW_MODEL_STATE"
        private const val CURSOR_POSITION = "CURSOR_POSITION"
        private const val TRACK_KEY = "TRACK"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L


    }
}