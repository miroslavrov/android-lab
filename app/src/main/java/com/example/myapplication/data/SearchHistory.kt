package com.example.myapplication.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.domain.Track
import com.example.myapplication.domain.TrackHistory
import kotlinx.coroutines.flow.first

private val Context.historyDataStore by preferencesDataStore(name = "playlist_maker_history")

internal class SearchHistory(context: Context, storage: Storage) : TrackHistory {
    private val tracksById = storage.tracks.associateBy(TrackDto::trackId)
    private val dataStore = context.historyDataStore

    override suspend fun getHistory(): List<Track> {
        val savedString = dataStore.data.first()[HISTORY_KEY].orEmpty()
        if (savedString.isEmpty()) return emptyList()
        val ids = savedString.split(",")
        return ids.mapNotNull { id -> tracksById[id]?.toTrack() }
    }

    override suspend fun addTrack(track: Track) {
        val currentHistory = getHistory().toMutableList()
        currentHistory.removeAll { it.trackId == track.trackId }
        currentHistory.add(0, track)
        if (currentHistory.size > 10) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }
        val newString = currentHistory.joinToString(",") { it.trackId }
        dataStore.edit { prefs -> prefs[HISTORY_KEY] = newString }
    }

    override suspend fun clearHistory() {
        dataStore.edit { prefs -> prefs.remove(HISTORY_KEY) }
    }

    private companion object {
        val HISTORY_KEY = stringPreferencesKey("history_tracks")
    }
}
