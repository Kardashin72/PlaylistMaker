package com.practicum.playlistmaker.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.App

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val THEME_PREFERENCES = "THEME_PREFERENCES"
        const val SWITCHER_KEY = "SWITCHER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //инициализация и обработка нажатия на свитчер темы с сохранением текущего
        //состояния в shared_preferences
        val theme_shared_prefs = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.switch_theme_button)
        themeSwitcher.isChecked = theme_shared_prefs.getBoolean(SWITCHER_KEY, false)


        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            theme_shared_prefs.edit()
                .putBoolean(SWITCHER_KEY, checked)
                .apply()
        }


        //инициализация и обработка нажатия на кнопку "назад"
        val backButton = findViewById<Button>(R.id.settings_back)
        backButton.setOnClickListener {
            finish()
        }

        //инициализация и обработка нажатия на кнопку "поделиться"
        val shareButton = findViewById<Button>(R.id.shareButton)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
                type = "text/plain"
            }
            val chooser = Intent.createChooser(shareIntent, getString(R.string.share_chooser_text))
            startActivity(chooser)
        }

        //инициализация и обработка нажатия на кнопку "написать в поддержку"
        val supportButton = findViewById<Button>(R.id.supportButton)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_message_topic))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            }
            if (supportIntent.resolveActivity(packageManager) != null) {
                startActivity(supportIntent)
            } else {
                Toast.makeText(this, R.string.mail_app_not_found, Toast.LENGTH_SHORT).show()
            }
        }

        //инициализация и обработка нажатия на кнопку "пользовательское соглашение"
        val userAgreementButton = findViewById<Button>(R.id.userAgreementButton)
        userAgreementButton.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW).apply {

                data = getString(R.string.user_agreement_link).toUri()
            }
            startActivity(userAgreementIntent)
        }
    }
}