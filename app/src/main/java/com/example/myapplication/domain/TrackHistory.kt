package com.example.myapplication.domain

interface TrackHistory {
    suspend fun getHistory(): List<Track>
    suspend fun addTrack(track: Track)
    suspend fun clearHistory()
}
