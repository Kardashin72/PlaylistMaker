package com.practicum.playlistmaker.settings.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsAction
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsViewModel
import com.practicum.playlistmaker.share.domain.model.ContactSupportData
import com.practicum.playlistmaker.share.domain.model.ShareAppData
import com.practicum.playlistmaker.share.domain.model.UserAgreementData
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel()
    private var isUserChangingSwitch = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isUserChangingSwitch = true
        setupClickListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupClickListeners() {
        //обработка нажатия на свитчер темы
        binding.switchThemeButton.setOnCheckedChangeListener { _, checked ->
            if (!isUserChangingSwitch) return@setOnCheckedChangeListener
            (requireContext().applicationContext as App).switchTheme(checked)
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
            type = "text/plain"
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
        if (supportIntent.resolveActivity(requireActivity().packageManager) != null) {
            val chooser = Intent.createChooser(supportIntent, getString(R.string.support_chooser_text))
            startActivity(chooser)
        } else {
            Toast.makeText(requireContext(), R.string.mail_app_not_found, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUserAgreement(data: UserAgreementData) {
        val userAgreementIntent = Intent(Intent.ACTION_VIEW).apply {
            setData(Uri.parse(data.userAgreementLink))
        }
        startActivity(userAgreementIntent)
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            isUserChangingSwitch = false
            binding.switchThemeButton.isChecked = state.isDarkTheme
            isUserChangingSwitch = true
        }

        viewModel.action.observe(viewLifecycleOwner) { action ->
            when (action) {
                is SettingsAction.ShareApp -> shareApp(action.data)
                is SettingsAction.ContactSupport -> contactSupport(action.data)
                is SettingsAction.OpenUserAgreement -> openUserAgreement(action.data)
            }
        }
    }
}