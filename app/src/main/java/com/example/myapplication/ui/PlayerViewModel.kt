package com.example.myapplication.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.FavoritesRepository
import com.example.myapplication.domain.Playlist
import com.example.myapplication.domain.PlaylistsRepository
import com.example.myapplication.domain.Track
import com.example.myapplication.domain.TracksRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val tracksRepository: TracksRepository,
    private val favoritesRepository: FavoritesRepository,
    private val playlistsRepository: PlaylistsRepository
) : ViewModel() {
    private val _track = MutableStateFlow<Track?>(null)
    val track = _track.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _positionMillis = MutableStateFlow(0)
    val positionMillis = _positionMillis.asStateFlow()

    private val _playbackFailed = MutableStateFlow(false)
    val playbackFailed = _playbackFailed.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    val playlists: StateFlow<List<Playlist>> = playlistsRepository.getPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var favoriteJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null
    private var prepared = false

    fun load(trackId: String) {
        if (_track.value?.trackId == trackId) return

        favoriteJob?.cancel()
        releasePlayer()
        _isFavorite.value = false
        _playbackFailed.value = false

        val track = tracksRepository.getTrack(trackId)
        _track.value = track
        if (track == null) return

        favoriteJob = viewModelScope.launch {
            favoritesRepository.isFavorite(trackId).collect { _isFavorite.value = it }
        }
        preparePlayer(track)
    }

    private fun preparePlayer(track: Track) {
        if (track.previewUrl.isBlank()) {
            _playbackFailed.value = true
            return
        }

        _playbackFailed.value = false
        runCatching {
            val player = MediaPlayer()
            mediaPlayer = player
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            player.setDataSource(track.previewUrl)
            player.setOnPreparedListener { readyPlayer ->
                if (readyPlayer === mediaPlayer) {
                    prepared = true
                    play(readyPlayer)
                }
            }
            player.setOnCompletionListener {
                if (it === mediaPlayer) {
                    _isPlaying.value = false
                    _positionMillis.value = 0
                    runCatching { it.seekTo(0) }.onFailure { playbackError() }
                }
            }
            player.setOnErrorListener { failedPlayer, _, _ ->
                if (failedPlayer === mediaPlayer) playbackError()
                true
            }
            player.prepareAsync()
        }.onFailure { playbackError() }
    }

    fun togglePlayPause() {
        val player = mediaPlayer
        if (player == null) {
            _track.value?.let(::preparePlayer)
            return
        }
        if (!prepared) return

        if (_isPlaying.value) {
            runCatching { player.pause() }
                .onSuccess { _isPlaying.value = false }
                .onFailure { playbackError() }
        } else {
            play(player)
        }
    }

    fun refreshPosition() {
        val player = mediaPlayer ?: return
        if (!prepared) return
        runCatching { player.currentPosition }
            .onSuccess { _positionMillis.value = it }
            .onFailure { playbackError() }
    }

    private fun play(player: MediaPlayer) {
        runCatching { player.start() }
            .onSuccess { _isPlaying.value = true }
            .onFailure { playbackError() }
    }

    private fun playbackError() {
        releasePlayer()
        _playbackFailed.value = true
    }

    fun toggleFavorite() {
        val current = _track.value ?: return
        viewModelScope.launch { favoritesRepository.toggleFavorite(current) }
    }

    fun addToPlaylist(playlistId: Long) {
        val current = _track.value ?: return
        viewModelScope.launch { playlistsRepository.addTrack(playlistId, current) }
    }

    private fun releasePlayer() {
        val player = mediaPlayer
        mediaPlayer = null
        player?.release()
        prepared = false
        _isPlaying.value = false
        _positionMillis.value = 0
    }

    override fun onCleared() {
        releasePlayer()
    }
}
