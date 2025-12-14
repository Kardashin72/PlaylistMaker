package com.practicum.playlistmaker.settings.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.google.android.material.R as MaterialR
import com.practicum.playlistmaker.core.presentation.ui.themeColor
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsScreenState,
    onThemeToggle: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onUserAgreementClick: () -> Unit,
) {
    Scaffold(
        containerColor = themeColor(attrRes = MaterialR.attr.colorPrimary),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.text_settings),
                        color = themeColor(attrRes = MaterialR.attr.colorOnPrimary),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeColor(attrRes = MaterialR.attr.colorPrimary),
                ),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 16.dp),
        ) {
            RowWithSwitch(
                title = stringResource(id = R.string.text_switch_dark_theme),
                checked = state.isDarkTheme,
                onCheckedChange = onThemeToggle,
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsMenuButton(
                text = stringResource(id = R.string.text_share_app),
                iconRes = R.drawable.share_icon,
                onClick = onShareClick,
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsMenuButton(
                text = stringResource(id = R.string.text_write_to_support),
                iconRes = R.drawable.support_icon,
                onClick = onSupportClick,
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsMenuButton(
                text = stringResource(id = R.string.text_user_agreement),
                iconRes = R.drawable.arrow_forward_icon,
                onClick = onUserAgreementClick,
            )
        }
    }
}

@Composable
private fun RowWithSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(themeColor(attrRes = MaterialR.attr.colorPrimary))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { onCheckedChange(!checked) },
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = themeColor(attrRes = MaterialR.attr.colorOnPrimary),
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun SettingsMenuButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
    androidx.compose.material3.Button(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = themeColor(attrRes = MaterialR.attr.colorPrimary),
            contentColor = themeColor(attrRes = MaterialR.attr.colorOnPrimary),
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp,
        ),
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            androidx.compose.material3.Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = themeColor(attrRes = MaterialR.attr.colorSecondaryVariant),
            )
        }
    }
}


