package com.example.myapplication.data

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.model.Track

class SearchHistory(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("playlist_maker_history", Context.MODE_PRIVATE)

    fun getHistory(): List<Track> {
        val savedString = sharedPreferences.getString("history_tracks", "") ?: ""
        if (savedString.isEmpty()) return emptyList()
        val ids = savedString.split(",")
        return ids.mapNotNull { id ->
            mockTracks.firstOrNull { it.trackId == id }
        }
    }

    fun addTrack(track: Track) {
        val currentHistory = getHistory().toMutableList()
        currentHistory.removeAll { it.trackId == track.trackId }
        currentHistory.add(0, track)
        if (currentHistory.size > 10) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }
        val newString = currentHistory.joinToString(",") { it.trackId }
        sharedPreferences.edit().putString("history_tracks", newString).apply()
    }

    fun clearHistory() {
        sharedPreferences.edit().remove("history_tracks").apply()
    }
}
