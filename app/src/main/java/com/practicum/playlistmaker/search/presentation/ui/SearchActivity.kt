package com.practicum.playlistmaker.search.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.presentation.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.api.TracksSearchHistoryInteractor
import com.practicum.playlistmaker.search.presentation.viewmodel.SearchScreenState
import com.practicum.playlistmaker.search.presentation.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTrack() }

    //список треков для RecycleViewAdapter
    var tracks = ArrayList<Track>()

    private lateinit var viewModel: SearchViewModel
    private lateinit var searchAdapter: SearchRecycleViewAdapter
    private lateinit var searchHistoryAdapter: SearchRecycleViewAdapter
    private lateinit var binding: ActivitySearchBinding


    //старт активити
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupClickListeners()
        setupRecyclerViews()
        setupTextWatcher()
        observeViewModel()
    }

    private fun updateUI(state: SearchScreenState) {
        when (state.screenStatus) {
            is SearchScreenState.ScreenStatus.Default -> {
                binding.apply {
                    progressBar.visibility = View.GONE
                    searchRecycleView.visibility = View.GONE
                    notFoundErrorMessage.visibility = View.GONE
                    connectionErrorMessage.visibility = View.GONE
                }
            }
            is SearchScreenState.ScreenStatus.Loading -> {
                binding.apply {
                    progressBar.visibility = View.VISIBLE
                    searchRecycleView.visibility = View.GONE
                    notFoundErrorMessage.visibility = View.GONE
                    connectionErrorMessage.visibility = View.GONE
                }
            }
            is SearchScreenState.ScreenStatus.LoadSuccess -> {
                binding.apply {
                    progressBar.visibility = View.GONE
                    searchRecycleView.visibility = View.VISIBLE
                    notFoundErrorMessage.visibility = View.GONE
                    connectionErrorMessage.visibility = View.GONE
                }
                searchAdapter.tracks = ArrayList(state.tracks)
                searchAdapter.notifyDataSetChanged()
            }
            is SearchScreenState.ScreenStatus.NotFoundError -> {
                binding.apply {
                    progressBar.visibility = View.GONE
                    searchRecycleView.visibility = View.GONE
                    notFoundErrorMessage.visibility = View.VISIBLE
                    connectionErrorMessage.visibility = View.GONE
                }
            }
            is SearchScreenState.ScreenStatus.ConnectionError -> {
                binding.apply {
                    progressBar.visibility = View.GONE
                    searchRecycleView.visibility = View.GONE
                    notFoundErrorMessage.visibility = View.GONE
                    connectionErrorMessage.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(this) { state ->
            updateUI(state)
        }
    }

    private fun setupViewModel() {
        val tracksSearchHistoryInteractor: TracksSearchHistoryInteractor by lazy {
            Creator.provideTracksSearchHistoryInteractor(this)
        }
        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getFactory(tracksSearchHistoryInteractor)
        ) [SearchViewModel :: class.java]
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
        binding.backButtonSearch.setOnClickListener {
            finish()
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
        }

        //обработка изменения состояния фокуса поля ввода текста
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()
                && viewModel.isHistoryNotEmpty()) {

                binding.historyView.visibility = View.VISIBLE
            } else {
                binding.historyView.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerViews() {
        //настройка адаптера и layoutManager для результатов поиска
        searchAdapter = SearchRecycleViewAdapter(tracks) { track ->
            viewModel.saveTrackToHistory(track)
            updateSearchHistory()
            if (clickDebounce()) {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra(INTENT_TRACK_KEY, track)
                startActivity(intent)
            }
        }
        binding.searchRecycleView.adapter = searchAdapter
        binding.searchRecycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //настройка адаптера и layoutManager для истории поиска
        searchHistoryAdapter = SearchRecycleViewAdapter(ArrayList()) { track ->
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(INTENT_TRACK_KEY, track)
            startActivity(intent)
        }
        binding.searchHistoryRecyclerView.adapter = searchHistoryAdapter
        binding.searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun setupTextWatcher() {
        //настройка TextWatcher
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
                binding.historyView.visibility =
                    if (binding.searchEditText.hasFocus()) View.VISIBLE else View.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearSearchTextButton.visibility = if (!s.isNullOrEmpty()) View.VISIBLE else View.GONE
                if (binding.searchEditText.hasFocus() && s?.isEmpty() == true && viewModel.isHistoryNotEmpty()) {
                    binding.historyView.visibility = View.VISIBLE
                } else {
                    binding.historyView.visibility = View.GONE
                }
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString() ?: ""
                viewModel.updateSearchText(text)
                if (text.isEmpty()) {
                    viewModel.clearSearchQuery()
                }
            }
        }

        //установка TextWatcher на EditText
        binding.searchEditText.addTextChangedListener(searchTextWatcher)
    }

    private fun updateSearchHistory() {
        searchHistoryAdapter.tracks = viewModel.getSearchHistory()
        searchHistoryAdapter.notifyDataSetChanged()
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
        const val INTENT_TRACK_KEY = "TRACK"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}