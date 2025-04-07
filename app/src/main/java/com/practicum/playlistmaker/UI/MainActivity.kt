package com.practicum.playlistmaker.UI
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_but)
        searchButton.setOnClickListener {
            val intentSearch = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intentSearch)
        }

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