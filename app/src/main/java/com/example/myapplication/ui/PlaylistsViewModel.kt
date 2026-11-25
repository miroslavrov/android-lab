package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.Playlist
import com.example.myapplication.domain.PlaylistsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsRepository: PlaylistsRepository
) : ViewModel() {
    val playlists: StateFlow<List<Playlist>> = playlistsRepository.getPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch { playlistsRepository.deletePlaylist(playlistId) }
    }

    private val _mergedPlaylists = MutableSharedFlow<Pair<String, String>>()
    val mergedPlaylists: SharedFlow<Pair<String, String>> = _mergedPlaylists

    fun mergePlaylists(source: Playlist, target: Playlist) {
        viewModelScope.launch {
            playlistsRepository.mergePlaylists(source.playlistId, target.playlistId)
            _mergedPlaylists.emit(source.name to target.name)
        }
    }
}
