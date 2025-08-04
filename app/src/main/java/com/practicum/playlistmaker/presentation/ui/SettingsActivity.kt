package com.practicum.playlistmaker.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.domain.api.SettingsInteractor

class SettingsActivity : AppCompatActivity() {
    private lateinit var settingsInteractor: SettingsInteractor
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsInteractor = Creator.provideSettingsInteractor(this)

        //обработка нажатия на кнопку "назад"
        binding.backButtonSettings.setOnClickListener {
            finish()
        }

        //обработка нажатия на свитчер темы
        binding.switchThemeButton
        binding.switchThemeButton.isChecked = settingsInteractor.isDarkTheme()

        binding.switchThemeButton.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            settingsInteractor.setDarkTheme(checked)
        }

        //обработка нажатия на кнопку "поделиться"
        binding.shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
                setType("text/plain")
            }
            val chooser = Intent.createChooser(shareIntent, getString(R.string.share_chooser_text))
            startActivity(chooser)
        }

        //обработка нажатия на кнопку "написать в поддержку"
        binding.supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SEND).apply {
                setType("message/rfc822")
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

        //обработка нажатия на кнопку "пользовательское соглашение"
        binding.userAgreementButton.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW).apply {

                setData(getString(R.string.user_agreement_link).toUri())
            }
            startActivity(userAgreementIntent)
        }
    }
}