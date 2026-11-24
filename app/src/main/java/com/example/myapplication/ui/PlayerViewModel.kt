package com.example.myapplication.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.Track
import com.example.myapplication.domain.TrackHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel(
    private val trackHistory: TrackHistory
) : ViewModel() {
    private val _track = MutableStateFlow<Track?>(null)
    val track = _track.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _positionMillis = MutableStateFlow(0)
    val positionMillis = _positionMillis.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    private val _inPlaylist = MutableStateFlow(false)
    val inPlaylist = _inPlaylist.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var prepared = false

    fun load(trackId: String) {
        if (_track.value?.trackId == trackId) return

        releasePlayer()
        _track.value = trackHistory.getHistory().firstOrNull { it.trackId == trackId }

        val url = _track.value?.previewUrl.orEmpty()
        if (url.isEmpty()) return

        runCatching {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener {
                    prepared = true
                    start()
                    _isPlaying.value = true
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    _positionMillis.value = 0
                    seekTo(0)
                }
                setOnErrorListener { _, _, _ ->
                    prepared = false
                    _isPlaying.value = false
                    true
                }
                prepareAsync()
            }
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (!prepared) return
        runCatching {
            if (player.isPlaying) {
                player.pause()
                _isPlaying.value = false
            } else {
                player.start()
                _isPlaying.value = true
            }
        }
    }

    fun refreshPosition() {
        _positionMillis.value = mediaPlayer?.takeIf { prepared }?.currentPosition ?: 0
    }

    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
    }

    fun toggleInPlaylist() {
        _inPlaylist.value = !_inPlaylist.value
    }

    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        prepared = false
        _isPlaying.value = false
        _positionMillis.value = 0
    }

    override fun onCleared() {
        releasePlayer()
    }
}
