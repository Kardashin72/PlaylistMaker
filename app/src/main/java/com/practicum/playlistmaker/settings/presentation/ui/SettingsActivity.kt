package com.practicum.playlistmaker.settings.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsAction
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsViewModel
import com.practicum.playlistmaker.share.domain.model.ContactSupportData
import com.practicum.playlistmaker.share.domain.model.ShareAppData
import com.practicum.playlistmaker.share.domain.model.UserAgreementData
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        //обработка нажатия на кнопку "назад"
        binding.backButtonSettings.setOnClickListener {
            finish()
        }

        //обработка нажатия на свитчер темы
        binding.switchThemeButton.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            viewModel.setDarkTheme(checked)
        }

        //обработка нажатия на кнопку "поделиться"
        binding.shareButton.setOnClickListener {
           viewModel.shareApp()
        }

        //обработка нажатия на кнопку "написать в поддержку"
        binding.supportButton.setOnClickListener {
            viewModel.contactSupport()
        }

        //обработка нажатия на кнопку "пользовательское соглашение"
        binding.userAgreementButton.setOnClickListener {
            viewModel.openUserAgreement()
        }
    }

    private fun shareApp(data: ShareAppData) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, data.shareLink)
            setType("text/plain")
        }
        val chooser = Intent.createChooser(shareIntent, data.chooserText)
        startActivity(chooser)
    }

    private fun contactSupport(data: ContactSupportData) {
        val supportIntent = Intent(Intent.ACTION_SEND).apply {
            setType("message/rfc822")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(data.supportEmail))
            putExtra(Intent.EXTRA_SUBJECT, data.messageTopic)
            putExtra(Intent.EXTRA_TEXT, data.message)
        }
        if (supportIntent.resolveActivity(packageManager) != null) {
            startActivity(supportIntent)
        } else {
            Toast.makeText(this, R.string.mail_app_not_found, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUserAgreement(data: UserAgreementData) {
        val userAgreementIntent = Intent(Intent.ACTION_VIEW).apply {
            setData(Uri.parse(data.userAgreementLink))
        }
        startActivity(userAgreementIntent)
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(this) { state ->
            binding.switchThemeButton.isChecked = state.isDarkTheme
        }

        viewModel.action.observe(this) { action ->
            when (action) {
                is SettingsAction.ShareApp -> shareApp(action.data)
                is SettingsAction.ContactSupport -> contactSupport(action.data)
                is SettingsAction.OpenUserAgreement -> openUserAgreement(action.data)
            }
        }
    }
}