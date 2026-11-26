package com.example.myapplication.data

import com.example.myapplication.data.db.TrackDao
import com.example.myapplication.domain.NetworkClient
import com.example.myapplication.domain.Track
import com.example.myapplication.domain.TracksRepository

internal class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val dao: TrackDao
) : TracksRepository {
    override suspend fun getTrack(trackId: String): Track? = dao.find(trackId)?.toTrack()

    override suspend fun searchTracks(query: String): List<Track> {
        val expression = query.trim()
        if (expression.isEmpty()) return emptyList()

        val tracks = networkClient.searchTracks(expression)
        if (tracks.isEmpty()) return emptyList()

        val ids = tracks.map(Track::trackId)
        dao.cache(tracks.map(Track::toMetadata))
        val stored = dao.findAll(ids).associateBy { it.trackId }
        return ids.mapNotNull { stored[it]?.toTrack() }
    }
}
