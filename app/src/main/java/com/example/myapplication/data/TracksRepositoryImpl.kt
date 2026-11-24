package com.example.myapplication.data

import com.example.myapplication.domain.NetworkClient
import com.example.myapplication.domain.Track
import com.example.myapplication.domain.TracksRepository
import java.io.IOException
import kotlinx.coroutines.delay

internal class TracksRepositoryImpl(
    private val storage: Storage,
    private val networkClient: NetworkClient
) : TracksRepository {
    override fun getTrack(trackId: String): Track? =
        storage.tracks.firstOrNull { it.trackId == trackId }?.toTrack()

    override suspend fun searchTracks(query: String): List<Track> {
        val expression = query.trim()
        if (expression.isEmpty()) return emptyList()

        delay(3000)
        if (!networkClient.isConnected()) throw IOException("Search connection unavailable")
        return storage.tracks.filter { track ->
            track.trackName.contains(expression, ignoreCase = true) ||
                track.artistName.contains(expression, ignoreCase = true)
        }.map(TrackDto::toTrack)
    }
}
