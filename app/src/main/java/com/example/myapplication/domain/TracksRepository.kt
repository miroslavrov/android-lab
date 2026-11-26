package com.example.myapplication.domain

interface TracksRepository {
    suspend fun searchTracks(query: String): List<Track>
    suspend fun getTrack(trackId: String): Track?
}
