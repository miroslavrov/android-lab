package com.example.myapplication.data

import com.example.myapplication.domain.Track
import java.util.Locale

internal data class TrackDto(
    val trackId: Long?,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    val previewUrl: String?
) {
    fun toTrack(): Track? {
        val id = trackId ?: return null
        val name = trackName?.takeIf { it.isNotBlank() } ?: return null
        val totalSeconds = (trackTimeMillis ?: 0L).coerceAtLeast(0L) / 1000
        return Track(
            trackId = id.toString(),
            trackName = name,
            artistName = artistName.orEmpty(),
            trackTime = String.format(Locale.ROOT, "%02d:%02d", totalSeconds / 60, totalSeconds % 60),
            artworkUrl = artworkUrl100.orEmpty(),
            previewUrl = previewUrl.orEmpty()
        )
    }
}
