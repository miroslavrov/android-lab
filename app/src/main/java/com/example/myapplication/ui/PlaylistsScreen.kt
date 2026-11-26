package com.example.myapplication.ui

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.R
import com.example.myapplication.domain.Playlist
import java.util.Locale
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    isDark: Boolean,
    onOpenPlaylist: (Long) -> Unit,
    viewModel: PlaylistsViewModel = koinViewModel()
) {
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    var actionPlaylist by remember { mutableStateOf<Playlist?>(null) }
    var deleteCandidate by remember { mutableStateOf<Playlist?>(null) }
    var mergeSource by remember { mutableStateOf<Playlist?>(null) }
    val context = LocalContext.current
    val resources = LocalResources.current

    LaunchedEffect(context, resources) {
        viewModel.mergedPlaylists.collect { (sourceName, targetName) ->
            val message = resources.getString(R.string.playlist_merge_success, sourceName, targetName)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (playlists.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.playlists_empty),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(playlists, key = Playlist::playlistId) { playlist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onOpenPlaylist(playlist.playlistId) },
                            onLongClick = { actionPlaylist = playlist }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isDark) Color(0xFF2C2C2E) else Color(0xFFE6E8EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = null,
                            tint = Color(0xFFAEAFB4),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = playlist.name,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = playlistTrackCountText(playlist.trackCount),
                            fontSize = 13.sp,
                            color = Color(0xFFAEAFB4)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFFAEAFB4),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    actionPlaylist?.let { playlist ->
        AlertDialog(
            onDismissRequest = { actionPlaylist = null },
            title = { Text(playlist.name) },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteCandidate = playlist
                        actionPlaylist = null
                    }
                ) { Text(stringResource(R.string.playlist_delete_title)) }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            mergeSource = playlist
                            actionPlaylist = null
                        }
                    ) { Text(stringResource(R.string.playlist_merge_action)) }
                    TextButton(onClick = { actionPlaylist = null }) {
                        Text(stringResource(R.string.playlist_delete_cancel))
                    }
                }
            }
        )
    }

    deleteCandidate?.let { playlist ->
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            title = { Text(stringResource(R.string.playlist_delete_title)) },
            text = { Text(stringResource(R.string.playlist_delete_message, playlist.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePlaylist(playlist.playlistId)
                        deleteCandidate = null
                    }
                ) { Text(stringResource(R.string.playlist_delete_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidate = null }) {
                    Text(stringResource(R.string.playlist_delete_cancel))
                }
            }
        )
    }

    mergeSource?.let { source ->
        ModalBottomSheet(
            onDismissRequest = { mergeSource = null },
            sheetState = rememberModalBottomSheetState()
        ) {
            Text(
                text = stringResource(R.string.playlist_merge_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyColumn {
                items(
                    playlists.filter { it.playlistId != source.playlistId },
                    key = { it.playlistId }
                ) { target ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.mergePlaylists(source, target)
                                mergeSource = null
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = null,
                            tint = Color(0xFFAEAFB4),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = target.name,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = playlistTrackCountText(target.trackCount),
                                fontSize = 13.sp,
                                color = Color(0xFFAEAFB4)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
internal fun playlistTrackCountText(count: Int): String {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    // The interface is Russian on devices that use another system language.
    val resources = remember(context, configuration) {
        val russianConfiguration = Configuration(configuration).apply {
            setLocale(Locale.forLanguageTag("ru"))
        }
        context.createConfigurationContext(russianConfiguration).resources
    }
    return resources.getQuantityString(R.plurals.playlist_track_count, count, count)
}
