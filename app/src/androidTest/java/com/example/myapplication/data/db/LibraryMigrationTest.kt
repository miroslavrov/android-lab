package com.example.myapplication.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class LibraryMigrationTest {
    @Test
    fun migrationPreservesPlaylistsAndFavoritesAcrossMetadataRefresh() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "migration-test-${UUID.randomUUID()}.db"
        createLegacyDatabase(context, name)
        val database = Room.databaseBuilder(context, AppDatabase::class.java, name)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
        try {
            val tracks = database.trackDao()
            val playlists = database.playlistDao()
            assertEquals("Saved", playlists.getPlaylist(1)?.name)
            assertEquals(2, playlists.getTrackCount(1).first())
            assertEquals(listOf("playlist-only", "favorite"), playlists.getTracks(1).first().map { it.trackId })
            assertEquals(listOf("favorite"), tracks.getFavorites().first().map { it.trackId })
            assertEquals(12L, tracks.find("favorite")?.favoriteAddedAt)

            tracks.cache(listOf(TrackMetadata("favorite", "Refreshed", "Artist", "03:00", "", "")))
            assertEquals("Refreshed", tracks.find("favorite")?.trackName)
            assertTrue(playlists.getTracks(1).first().single { it.trackId == "favorite" }.isFavorite)
            tracks.removeFavorite("favorite")
            assertFalse(playlists.getTracks(1).first().single { it.trackId == "favorite" }.isFavorite)
            assertTrue(tracks.getFavorites().first().isEmpty())

            val targetId = playlists.insertPlaylist(PlaylistEntity(name = "Target", description = ""))
            playlists.insertTrackIntoPlaylist(PlaylistTrackEntity(targetId, "favorite"))
            playlists.mergePlaylists(1, targetId)
            assertNull(playlists.getPlaylist(1))
            assertEquals(0, playlists.getTrackCount(1).first())
            assertEquals(2, playlists.getTrackCount(targetId).first())
            playlists.deletePlaylist(targetId)
            assertEquals(0, playlists.getTrackCount(targetId).first())
            assertEquals("Refreshed", tracks.find("favorite")?.trackName)
        } finally {
            database.close()
            context.deleteDatabase(name)
        }
    }

    private fun createLegacyDatabase(context: Context, name: String) {
        val file = context.getDatabasePath(name)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { database ->
            database.execSQL("""
                CREATE TABLE playlists (
                    playlistId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL, description TEXT NOT NULL
                )
            """)
            database.execSQL("""
                CREATE TABLE playlist_tracks (
                    playlistId INTEGER NOT NULL, trackId TEXT NOT NULL, trackName TEXT NOT NULL,
                    artistName TEXT NOT NULL, trackTime TEXT NOT NULL, artworkUrl TEXT NOT NULL,
                    previewUrl TEXT NOT NULL, addedAt INTEGER NOT NULL,
                    PRIMARY KEY (playlistId, trackId)
                )
            """)
            database.execSQL("""
                CREATE TABLE favorite_tracks (
                    trackId TEXT NOT NULL PRIMARY KEY, trackName TEXT NOT NULL, artistName TEXT NOT NULL,
                    trackTime TEXT NOT NULL, artworkUrl TEXT NOT NULL, previewUrl TEXT NOT NULL,
                    addedAt INTEGER NOT NULL
                )
            """)
            database.execSQL("INSERT INTO playlists VALUES (1, 'Saved', 'Description')")
            database.execSQL("INSERT INTO playlist_tracks VALUES (1, 'favorite', 'One', 'Artist', '03:00', '', '', 10)")
            database.execSQL("INSERT INTO playlist_tracks VALUES (1, 'playlist-only', 'Two', 'Artist', '02:00', '', '', 11)")
            database.execSQL("INSERT INTO favorite_tracks VALUES ('favorite', 'One', 'Artist', '03:00', '', '', 12)")
            database.version = 1
        }
    }
}
