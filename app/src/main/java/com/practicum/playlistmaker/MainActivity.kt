package com.practicum.playlistmaker
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_but)
        val searchButtonClickListener: View.OnClickListener = object  : View.OnClickListener {
            override fun onClick(v: View?) {
                val intentSearch = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(intentSearch)
            }
        }
        searchButton.setOnClickListener(searchButtonClickListener)

        val mediaLibraryButton = findViewById<Button>(R.id.media_library_button)
        mediaLibraryButton.setOnClickListener {
            val mediaLibraryIntent = Intent(this@MainActivity, MediaLibraryActivity::class.java)
            startActivity(mediaLibraryIntent)
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}