package com.example.myapplication.data

import com.example.myapplication.domain.NetworkClient
import com.example.myapplication.domain.SearchServerException
import com.example.myapplication.domain.Track
import retrofit2.HttpException

internal class NetworkClientImpl(private val api: ItunesApi) : NetworkClient {
    override suspend fun searchTracks(query: String): List<Track> {
        return try {
            api.search(query).results.orEmpty().mapNotNull(TrackDto::toTrack)
                .distinctBy(Track::trackId)
        } catch (error: HttpException) {
            throw SearchServerException(error.code())
        }
    }
}
