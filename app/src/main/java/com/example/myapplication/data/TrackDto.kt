package com.example.myapplication.data

import com.example.myapplication.domain.Track
import java.util.Locale

internal data class TrackDto(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl: String
) {
    fun toTrack(): Track {
        val totalSeconds = trackTimeMillis / 1000
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTime = String.format(Locale.ROOT, "%02d:%02d", totalSeconds / 60, totalSeconds % 60),
            artworkUrl = artworkUrl
        )
    }
}
