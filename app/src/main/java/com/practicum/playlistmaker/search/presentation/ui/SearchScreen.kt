package com.practicum.playlistmaker.search.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.google.android.material.R as MaterialR
import com.practicum.playlistmaker.core.presentation.ui.themeColor
import com.practicum.playlistmaker.core.presentation.ui.components.TrackItem
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.presentation.viewmodel.SearchScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchScreenState,
    history: List<Track>,
    queryText: String,
    onQueryChange: (String) -> Unit,
    onSearchAction: () -> Unit,
    onClearQueryClick: () -> Unit,
    onClearHistoryClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    onHistoryTrackClick: (Track) -> Unit,
) {
    Scaffold(
        containerColor = themeColor(attrRes = MaterialR.attr.colorPrimary),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.text_search),
                        color = themeColor(attrRes = MaterialR.attr.colorOnPrimary),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeColor(attrRes = MaterialR.attr.colorPrimary),
                ),
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                SearchTextField(
                    value = queryText,
                    onValueChange = onQueryChange,
                    onSearchAction = onSearchAction,
                    onClearClick = onClearQueryClick,
                )

                if (state.isSearchHistoryVisible && history.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SearchHistorySection(
                        history = history,
                        onClearHistoryClick = onClearHistoryClick,
                        onItemClick = onHistoryTrackClick,
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                    SearchContent(
                        state = state,
                        onRefreshClick = onRefreshClick,
                        onTrackClick = onTrackClick,
                    )
                }
            }

            if (state.screenStatus is SearchScreenState.ScreenStatus.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

//это делал с нейросетью, потому что стандартный TextField требует высоту больше, чем в макетах,
//а при ограничении высоты до нужных 36dp - обрезает содержимое
@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchAction: () -> Unit,
    onClearClick: () -> Unit,
) {
    val backgroundColor = themeColor(attrRes = R.attr.colorSearchBackground)
    val hintColor = themeColor(attrRes = R.attr.colorSearchHintText)
    val textColor = themeColor(attrRes = MaterialR.attr.colorOnPrimary)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp),
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.small_search_icon),
                contentDescription = null,
                tint = hintColor,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.text_search),
                        color = hintColor,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = textColor,
                        fontSize = 16.sp,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearchAction() }),
                    cursorBrush = SolidColor(colorResource(id = R.color.YP_Blue)),
                )
            }

            if (value.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.cross),
                        contentDescription = null,
                        tint = hintColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchHistorySection(
    history: List<Track>,
    onClearHistoryClick: () -> Unit,
    onItemClick: (Track) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = stringResource(id = R.string.search_history_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            items(history, key = { it.trackId }) { track ->
                TrackItem(
                    track = track,
                    onClick = { onItemClick(track) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClearHistoryClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(text = stringResource(id = R.string.search_history_clear))
        }
    }
}

@Composable
private fun SearchContent(
    state: SearchScreenState,
    onRefreshClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
) {
    when (state.screenStatus) {
        is SearchScreenState.ScreenStatus.Default -> Unit
        is SearchScreenState.ScreenStatus.LoadSuccess -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(state.tracks, key = { it.trackId }) { track ->
                    TrackItem(
                        track = track,
                        onClick = { onTrackClick(track) },
                    )
                }
            }
        }
        is SearchScreenState.ScreenStatus.NotFoundError -> {
            SearchMessage(
                iconRes = R.drawable.not_found_image,
                textRes = R.string.search_not_found,
            )
        }
        is SearchScreenState.ScreenStatus.ConnectionError -> {
            SearchMessageWithButton(
                iconRes = R.drawable.search_connection_error_image,
                textRes = R.string.search_not_connection,
                buttonTextRes = R.string.search_refresh,
                onButtonClick = onRefreshClick,
            )
        }
        is SearchScreenState.ScreenStatus.Loading -> Unit
    }
}

@Composable
private fun SearchMessage(
    iconRes: Int,
    textRes: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = textRes),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SearchMessageWithButton(
    iconRes: Int,
    textRes: Int,
    buttonTextRes: Int,
    onButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = textRes),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onButtonClick) {
            Text(text = stringResource(id = buttonTextRes))
        }
    }
}


