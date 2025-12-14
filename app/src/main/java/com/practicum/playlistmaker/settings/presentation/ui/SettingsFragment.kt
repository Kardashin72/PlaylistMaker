package com.practicum.playlistmaker.settings.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsAction
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsScreenState
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsViewModel
import com.practicum.playlistmaker.share.domain.model.ContactSupportData
import com.practicum.playlistmaker.share.domain.model.ShareAppData
import com.practicum.playlistmaker.share.domain.model.UserAgreementData
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state: SettingsScreenState by viewModel.screenState.observeAsState(
                    initial = SettingsScreenState()
                )

                SettingsScreen(
                    state = state,
                    onThemeToggle = { isDark ->
                        (requireContext().applicationContext as App).switchTheme(isDark)
                        viewModel.setDarkTheme(isDark)
                    },
                    onShareClick = { viewModel.shareApp() },
                    onSupportClick = { viewModel.contactSupport() },
                    onUserAgreementClick = { viewModel.openUserAgreement() },
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModelActions()
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

    private fun observeViewModelActions() {
        viewModel.action.observe(viewLifecycleOwner) { action ->
            when (action) {
                is SettingsAction.ShareApp -> shareApp(action.data)
                is SettingsAction.ContactSupport -> contactSupport(action.data)
                is SettingsAction.OpenUserAgreement -> openUserAgreement(action.data)
            }
        }
    }
}