package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

class SearchActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchInputText = findViewById<EditText>(R.id.search_edit_text)
        val clearText = findViewById<Button>(R.id.clear_text_button)
        val backButton = findViewById<Button>(R.id.search_back_buttton)

        backButton.setOnClickListener {
            val intentBack = Intent(this@SearchActivity, MainActivity::class.java )
            startActivity(intentBack)
        }

        clearText.setOnClickListener {
            searchInputText.text.clear()
            hideKeyboard(searchInputText)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //заглушка
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearText.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                //заглушка
            }
        }
        searchInputText.addTextChangedListener(searchTextWatcher)
    }

    //функция, отвечающая за видимость кнопки очистки строки ввода
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    //функция, отвечающая за отключение клавиатуры при нажатии на кнопку очистки строки ввода
    //писал с помощью ИИ, так как в теории не нашел, как это сделать
    private fun hideKeyboard(view: View) {
        val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
}