package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.R
import com.example.myapplication.domain.Track
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    isDark: Boolean,
    onOpenPlayer: (Track) -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val searchQuery by viewModel.query.collectAsStateWithLifecycle()
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val historyTracks by viewModel.history.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val onSearch = {
        focusManager.clearFocus()
        viewModel.search()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.search_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isDark) Color(0xFF2C2C2E) else Color(0xFFE6E8EB)
                    )
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_action),
                        tint = Color(0xFFAEAFB4),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(
                                enabled = searchQuery.isNotBlank(),
                                role = Role.Button,
                                onClick = onSearch
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_title),
                                color = Color(0xFFAEAFB4),
                                fontSize = 16.sp
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = viewModel::updateQuery,
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.search_clear),
                            tint = Color(0xFFAEAFB4),
                            modifier = Modifier
                                .size(18.dp)
                                .clickable(role = Role.Button) {
                                    viewModel.updateQuery("")
                                    focusManager.clearFocus()
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = searchState) {
                SearchState.Initial -> {
                    if (searchQuery.isEmpty() && historyTracks.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.search_history),
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                            TrackList(
                                tracks = historyTracks,
                                isDark = isDark,
                                onTrackClick = { track ->
                                    viewModel.rememberTrack(track)
                                    onOpenPlayer(track)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            SearchButton(
                                text = stringResource(R.string.search_clear_history),
                                onClick = viewModel::clearHistory,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                }

                SearchState.Searching -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is SearchState.Success -> {
                    if (state.tracks.isEmpty()) {
                        SearchMessage(
                            icon = Icons.Default.SearchOff,
                            title = stringResource(R.string.search_no_results)
                        )
                    } else {
                        TrackList(
                            tracks = state.tracks,
                            isDark = isDark,
                            onTrackClick = { track ->
                                viewModel.rememberTrack(track)
                                onOpenPlayer(track)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                SearchState.Fail -> {
                    SearchMessage(
                        icon = Icons.Default.SignalWifiOff,
                        title = stringResource(R.string.search_connection_title),
                        description = stringResource(R.string.search_connection_description),
                        onRetry = onSearch
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackList(
    tracks: List<Track>,
    isDark: Boolean,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(tracks, key = Track::trackId) { track ->
            TrackItem(track, isDark, onClick = { onTrackClick(track) })
        }
    }
}

@Composable
private fun SearchMessage(
    icon: ImageVector,
    title: String,
    description: String? = null,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
        Text(
            text = title,
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        if (description != null) {
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFFAEAFB4),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
        if (onRetry != null) {
            SearchButton(text = stringResource(R.string.search_retry), onClick = onRetry)
        }
    }
}

@Composable
private fun SearchButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3772E7),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(54.dp),
        modifier = modifier.height(44.dp)
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun TrackItem(
    track: Track,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TrackArtwork(
            url = track.artworkUrl,
            isDark = isDark,
            cornerRadius = 2.dp,
            placeholderSize = 24.dp,
            modifier = Modifier
                .size(45.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.trackName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.artistName,
                    fontSize = 13.sp,
                    color = Color(0xFFAEAFB4),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.search_track_separator),
                    fontSize = 13.sp,
                    color = Color(0xFFAEAFB4)
                )
                Text(
                    text = track.trackTime,
                    fontSize = 13.sp,
                    color = Color(0xFFAEAFB4)
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFAEAFB4),
            modifier = Modifier.size(20.dp)
        )
    }
}
