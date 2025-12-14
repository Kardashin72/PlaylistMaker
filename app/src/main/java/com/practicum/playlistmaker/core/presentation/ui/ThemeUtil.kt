package com.practicum.playlistmaker.core.presentation.ui

import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun themeColor(@AttrRes attrRes: Int): Color {
    val context = LocalContext.current
    val typedValue = TypedValue()
    val theme = context.theme
    return if (theme.resolveAttribute(attrRes, typedValue, true)) {
        Color(typedValue.data)
    } else {
        Color.Unspecified
    }
}