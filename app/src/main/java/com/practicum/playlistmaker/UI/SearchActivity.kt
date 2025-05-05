package com.practicum.playlistmaker.UI

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.SearchHistory
import com.practicum.playlistmaker.data.Track
import com.practicum.playlistmaker.data.TrackSearchResponse
import com.practicum.playlistmaker.data.dtoTracksToTrackList
import com.practicum.playlistmaker.data.searchApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchActivity : AppCompatActivity() {
    companion object {
        private const val EDIT_TEXT_KEY = "EDIT_TEXT_KEY"
        private const val CURSOR_POSITION = "CURSOR_POSITION"
        private const val HISTORY_PREFERENCES = "HISTORY_PREFERENCES"
        private const val HISTORY_PREFERENCES_KEY = "HISTORY_PREFERENCES_KEY"
    }

    //список треков для RecycleViewAdapter
    var trackList = ArrayList<Track>()

    //инициализация всех View
    private lateinit var recycleView: RecyclerView
    private lateinit var historyView: LinearLayout
    private lateinit var searchHistoryView: RecyclerView
    private lateinit var searchAdapter: SearchRecycleViewAdapter
    private lateinit var historyAdapter: SearchRecycleViewAdapter
    private lateinit var notFoundMessage: LinearLayout
    private lateinit var searchConnectionErrorMessage: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var searchEditText: EditText
    private lateinit var clearText: Button
    private lateinit var backButton: Button
    private lateinit var clearSearchHistory: Button

    //переменная для сохранения состояния активити
    private var savedText = ""

    //старт активити
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //присвоение View по ID
        searchEditText = findViewById<EditText>(R.id.search_edit_text)
        clearText = findViewById<Button>(R.id.clear_text_button)
        backButton = findViewById<Button>(R.id.search_back_buttton)
        refreshButton = findViewById<Button>(R.id.search_refresh_button)
        notFoundMessage = findViewById(R.id.not_found_error)
        searchConnectionErrorMessage = findViewById(R.id.search_connection_error)
        recycleView = findViewById(R.id.search_recycle_view)
        historyView = findViewById(R.id.history_view)
        searchHistoryView = findViewById(R.id.search_history)
        clearSearchHistory = findViewById(R.id.clear_search_history)

        //инициализация shared perferences и создание экземпляра SearchHistory
        val sharedPreferences = getSharedPreferences(HISTORY_PREFERENCES, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPreferences)

        //обработка изменения состояния фокуса поля ввода текста
        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()
                && searchHistory.hasHistory(HISTORY_PREFERENCES_KEY)) {
                historyAdapter.tracks = searchHistory.loadSearchHistory(HISTORY_PREFERENCES_KEY)
                historyAdapter.notifyDataSetChanged()
                historyView.visibility = View.VISIBLE
            } else {
                historyView.visibility = View.GONE
            }
        }


        //настройка адаптера и layoutManager для результатов поиска
        searchAdapter = SearchRecycleViewAdapter(trackList) { track ->
            searchHistory.saveTrackToHistory(track, HISTORY_PREFERENCES_KEY)
            historyAdapter.tracks = searchHistory.loadSearchHistory(HISTORY_PREFERENCES_KEY)
            historyAdapter.notifyDataSetChanged()
        }
        recycleView.adapter = searchAdapter
        recycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //настройка адаптера и layoutManager для истории поиска
        historyAdapter = SearchRecycleViewAdapter(searchHistory.loadSearchHistory(HISTORY_PREFERENCES_KEY)) {
                TODO()
        }
        searchHistoryView.adapter = historyAdapter
        searchHistoryView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        clearSearchHistory.setOnClickListener {
            searchHistory.clearSearchHistory()
            historyView.visibility = View.GONE
        }

        //обработка нажатия на кнопку "Назад"
        backButton.setOnClickListener {
            finish()
        }

        //обработка нажатия на кнопку очистки строки ввода
        clearText.setOnClickListener {
            searchEditText.text.clear()
            trackList.clear()
            hideKeyboard(searchEditText)
            recycleView.visibility = View.GONE
            notFoundMessage.visibility = View.GONE
            searchConnectionErrorMessage.visibility = View.GONE
        }

        //обработка нажатия на кнопку "Обновить" в случае отсутствия интернета
        refreshButton.setOnClickListener {
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
                historyView.visibility =
                    if (searchEditText.hasFocus()) View.VISIBLE else View.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearText.visibility = if (!s.isNullOrEmpty()) View.VISIBLE else View.GONE
                if (searchEditText.hasFocus() && s?.isEmpty() == true && searchHistory.hasHistory(HISTORY_PREFERENCES_KEY) == true) {
                    historyView.visibility = View.VISIBLE
                } else {
                    historyView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                savedText = s?.toString() ?: ""
                if (savedText.isEmpty()) {
                    trackList.clear()
                    recycleView.visibility = View.GONE
                    notFoundMessage.visibility = View.GONE
                    searchConnectionErrorMessage.visibility = View.GONE
                }
            }
        }

        //установка TextWatcher на EditText
        searchEditText.addTextChangedListener(searchTextWatcher)

        //обработка нажатия на кнопку "ОК" экранной клавиатуры
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
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
        outState.putString(EDIT_TEXT_KEY, searchEditText.text.toString())
        outState.putInt(CURSOR_POSITION, searchEditText.selectionStart)
    }

    //восстановление строки ввода из сохраненного Bundle
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedText = savedInstanceState.getString(EDIT_TEXT_KEY) ?: ""
        val cursorPosition = savedInstanceState.getInt(CURSOR_POSITION, 0)
        searchEditText.setText(savedText)
        searchEditText.setSelection(cursorPosition.coerceIn(0, savedText.length))
    }

    //функция, отвечающая за отключение клавиатуры при нажатии на кнопку очистки строки ввода
    private fun hideKeyboard(view: View) {
        val inputManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    //функция поиска трека через API
    private fun searchTrack() {
        searchApiService.search(searchEditText.text.toString())
            .enqueue(object : Callback<TrackSearchResponse> {
                override fun onResponse(
                    call: Call<TrackSearchResponse>,
                    response: Response<TrackSearchResponse>,
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.dtoTracks?.isNotEmpty() == true) {
                            trackList.clear()
                            trackList.addAll(dtoTracksToTrackList(response.body()))
                            searchAdapter.notifyDataSetChanged()
                            notFoundMessage.visibility = View.GONE
                            searchConnectionErrorMessage.visibility = View.GONE
                            recycleView.visibility = View.VISIBLE
                        } else {
                            trackList.clear()
                            recycleView.visibility = View.GONE
                            searchConnectionErrorMessage.visibility = View.GONE
                            notFoundMessage.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<TrackSearchResponse>, t: Throwable) {
                    searchConnectionErrorMessage.visibility = View.VISIBLE
                    recycleView.visibility = View.GONE
                    notFoundMessage.visibility = View.GONE
                }
            })
    }
}