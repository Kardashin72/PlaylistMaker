package com.practicum.playlistmaker.UI

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.trackList
import com.bumptech.glide.Glide

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val EDIT_TEXT_KEY = "EDIT_TEXT_KEY"
        private const val CURSOR_POSITION = "CURSOR_POSITION"
    }

    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: SearchRecycleViewAdapter

    //переменная объявлена вне функции onCreate, чтобы доступ к ней был в функции onSaveInstanceState
    private lateinit var searchEditText: EditText
    private var savedText = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val clearText = findViewById<Button>(R.id.clear_text_button)
        val backButton = findViewById<Button>(R.id.search_back_buttton)

        backButton.setOnClickListener {
            finish()
        }

        clearText.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard(searchEditText)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //заглушка
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearText.visibility = if (!s.isNullOrEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                savedText = s?.toString() ?: ""
            }
        }
        searchEditText.addTextChangedListener(searchTextWatcher)

        recycleView = findViewById(R.id.search_recycle_view)
        adapter = SearchRecycleViewAdapter(trackList)
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
    //писал с помощью ИИ, так как в теории не нашел, как это сделать
    private fun hideKeyboard(view: View) {
        val inputManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }




}