package com.example.myapplication.domain

interface TrackHistory {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}
