package com.example.myapplication.data

import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.domain.Track
import com.example.myapplication.domain.TrackHistory
import kotlinx.coroutines.flow.first

private const val HISTORY_STORE = "playlist_maker_history"
private const val HISTORY_PREF = "history_tracks"

private val Context.historyDataStore by preferencesDataStore(
    name = HISTORY_STORE,
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, HISTORY_STORE, setOf(HISTORY_PREF)))
    }
)

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
        dataStore.edit { prefs ->
            val previousIds = prefs[HISTORY_KEY].orEmpty()
                .split(',')
                .filter { it != track.trackId && it in tracksById }
            prefs[HISTORY_KEY] = (listOf(track.trackId) + previousIds).take(10).joinToString(",")
        }
    }

    override suspend fun clearHistory() {
        dataStore.edit { prefs -> prefs.remove(HISTORY_KEY) }
    }

    private companion object {
        val HISTORY_KEY = stringPreferencesKey(HISTORY_PREF)
    }
}
