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
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.presentation.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.api.SearchResult
import com.practicum.playlistmaker.search.domain.api.TracksSearchHistoryInteractor

class SearchActivity : AppCompatActivity() {
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTrack() }

    //список треков для RecycleViewAdapter
    var tracks = ArrayList<Track>()

    private lateinit var searchAdapter: SearchRecycleViewAdapter
    private lateinit var searchHistoryAdapter: SearchRecycleViewAdapter
    private lateinit var binding: ActivitySearchBinding
    private val tracksSearchHistoryInteractor: TracksSearchHistoryInteractor by lazy {
        Creator.provideTracksSearchHistoryInteractor(this)
    }
    private val tracksSearchInteractor: TracksSearchInteractor by lazy {
        Creator.provideTracksSearchInteractor()
    }

    //переменная для сохранения состояния активити
    private var savedText = ""

    //старт активити
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //обработка изменения состояния фокуса поля ввода текста
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()
                && tracksSearchHistoryInteractor.hasHistory()) {
                searchHistoryAdapter.tracks = ArrayList(tracksSearchHistoryInteractor.loadSearchHistory())
                searchHistoryAdapter.notifyDataSetChanged()
                binding.historyView.visibility = View.VISIBLE
            } else {
                binding.historyView.visibility = View.GONE
            }
        }

        //настройка адаптера и layoutManager для результатов поиска
        searchAdapter = SearchRecycleViewAdapter(tracks) { track ->
            tracksSearchHistoryInteractor.saveTrackToHistory(track)
            searchHistoryAdapter.tracks = ArrayList(tracksSearchHistoryInteractor.loadSearchHistory())
            searchHistoryAdapter.notifyDataSetChanged()
            if (clickDebounce()) {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra(INTENT_TRACK_KEY, track)
                startActivity(intent)
            }
        }
        binding.searchRecycleView.adapter = searchAdapter
        binding.searchRecycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //настройка адаптера и layoutManager для истории поиска
        searchHistoryAdapter =
            SearchRecycleViewAdapter(ArrayList(tracksSearchHistoryInteractor.loadSearchHistory())) { track ->
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra(INTENT_TRACK_KEY, track)
                startActivity(intent)
            }

        binding.searchHistoryRecyclerView.adapter = searchHistoryAdapter
        binding.searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.clearSearchHistoryButton.setOnClickListener {
            tracksSearchHistoryInteractor.clearSearchHistory()
            binding.searchHistoryRecyclerView.visibility = View.GONE
        }

        //обработка нажатия на кнопку "Назад"
        binding.backButtonSearch.setOnClickListener {
            finish()
        }

        //обработка нажатия на кнопку очистки строки ввода
        binding.clearSearchTextButton.setOnClickListener {
            binding.searchEditText.text.clear()
            tracks.clear()
            hideKeyboard(binding.searchEditText)
            binding.searchRecycleView.visibility = View.GONE
            binding.notFoundErrorMessage.visibility = View.GONE
            binding.connectionErrorMessage.visibility = View.GONE
        }

        //обработка нажатия на кнопку "Обновить" в случае отсутствия интернета
        binding.searchRefreshButton.setOnClickListener {
            searchTrack()
        }

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
                if (binding.searchEditText.hasFocus() && s?.isEmpty() == true && tracksSearchHistoryInteractor.hasHistory()) {
                    binding.historyView.visibility = View.VISIBLE
                } else {
                    binding.historyView.visibility = View.GONE
                }
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                savedText = s?.toString() ?: ""
                if (savedText.isEmpty()) {
                    tracks.clear()
                    binding.searchRecycleView.visibility = View.GONE
                    binding.notFoundErrorMessage.visibility = View.GONE
                    binding.connectionErrorMessage.visibility = View.GONE
                }
            }
        }

        //установка TextWatcher на EditText
        binding.searchEditText.addTextChangedListener(searchTextWatcher)

        //обработка нажатия на кнопку "ОК" экранной клавиатуры
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }
    }

    //сохранение текста из строки ввода
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_KEY, binding.searchEditText.text.toString())
        outState.putInt(CURSOR_POSITION, binding.searchEditText.selectionStart)
    }

    //восстановление строки ввода из сохраненного Bundle
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedText = savedInstanceState.getString(EDIT_TEXT_KEY) ?: ""
        val cursorPosition = savedInstanceState.getInt(CURSOR_POSITION, 0)
        binding.searchEditText.setText(savedText)
        binding.searchEditText.setSelection(cursorPosition.coerceIn(0, savedText.length))
    }

    //функция, отвечающая за отключение клавиатуры при нажатии на кнопку очистки строки ввода
    private fun hideKeyboard(view: View) {
        val inputManager =
            view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    //функция поиска трека через API
    private fun searchTrack() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            searchRecycleView.visibility = View.GONE
            notFoundErrorMessage.visibility = View.GONE
            connectionErrorMessage.visibility = View.GONE
        }
        tracksSearchInteractor.searchTracks(binding.searchEditText.text.toString(), object : TracksSearchInteractor.TracksConsumer {
            override fun consume(result: SearchResult) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    when (result) {
                        is SearchResult.Success -> {
                            if (result.tracks.isNotEmpty()) {
                                tracks.clear()
                                tracks.addAll(result.tracks)
                                searchAdapter.notifyDataSetChanged()
                                binding.apply {
                                    notFoundErrorMessage.visibility = View.GONE
                                    connectionErrorMessage.visibility = View.GONE
                                    searchRecycleView.visibility = View.VISIBLE
                                }
                            } else {
                                tracks.clear()
                                binding.apply {
                                    searchRecycleView.visibility = View.GONE
                                    connectionErrorMessage.visibility = View.GONE
                                    notFoundErrorMessage.visibility = View.VISIBLE
                                }
                            }
                        }
                        is SearchResult.ConnectionError -> {
                            binding.apply {
                                connectionErrorMessage.visibility = View.VISIBLE
                                searchRecycleView.visibility = View.GONE
                                notFoundErrorMessage.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        })
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
        private const val EDIT_TEXT_KEY = "EDIT_TEXT_KEY"
        private const val CURSOR_POSITION = "CURSOR_POSITION"
        private const val HISTORY_PREFERENCES_KEY = "HISTORY_PREFERENCES_KEY"
        const val INTENT_TRACK_KEY = "TRACK"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}