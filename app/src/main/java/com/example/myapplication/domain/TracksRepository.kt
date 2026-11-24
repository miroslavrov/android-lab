package com.example.myapplication.domain

interface TracksRepository {
    suspend fun searchTracks(query: String): List<Track>
    fun getTrack(trackId: String): Track?
}
