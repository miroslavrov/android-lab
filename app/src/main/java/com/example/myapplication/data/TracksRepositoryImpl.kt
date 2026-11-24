package com.example.myapplication.data

import com.example.myapplication.domain.Track
import com.example.myapplication.domain.TracksRepository
import kotlinx.coroutines.delay

internal class TracksRepositoryImpl(private val storage: Storage) : TracksRepository {
    override suspend fun searchTracks(query: String): List<Track> {
        val expression = query.trim()
        if (expression.isEmpty()) return emptyList()

        delay(3000)
        return storage.tracks.filter { track ->
            track.trackName.contains(expression, ignoreCase = true) ||
                track.artistName.contains(expression, ignoreCase = true)
        }.map(TrackDto::toTrack)
    }
}
