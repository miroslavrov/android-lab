package com.example.myapplication.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [PlaylistEntity::class, PlaylistTrackEntity::class, TrackEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackDao(): TrackDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS tracks (
                        trackId TEXT NOT NULL PRIMARY KEY, trackName TEXT NOT NULL,
                        artistName TEXT NOT NULL, trackTime TEXT NOT NULL,
                        artworkUrl TEXT NOT NULL, previewUrl TEXT NOT NULL,
                        isFavorite INTEGER NOT NULL DEFAULT 0, favoriteAddedAt INTEGER
                    )
                """)
                db.execSQL("""
                    INSERT INTO tracks (trackId, trackName, artistName, trackTime,
                        artworkUrl, previewUrl, isFavorite, favoriteAddedAt)
                    SELECT trackId, trackName, artistName, trackTime, artworkUrl, previewUrl, 1, addedAt
                    FROM favorite_tracks
                """)
                db.execSQL("""
                    INSERT OR IGNORE INTO tracks (trackId, trackName, artistName, trackTime, artworkUrl, previewUrl)
                    SELECT trackId, trackName, artistName, trackTime, artworkUrl, previewUrl FROM playlist_tracks
                """)
                db.execSQL("""
                    CREATE TABLE playlist_tracks_new (
                        playlistId INTEGER NOT NULL, trackId TEXT NOT NULL, addedAt INTEGER NOT NULL,
                        PRIMARY KEY (playlistId, trackId),
                        FOREIGN KEY (playlistId) REFERENCES playlists (playlistId) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY (trackId) REFERENCES tracks (trackId) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """)
                db.execSQL("""
                    INSERT INTO playlist_tracks_new (playlistId, trackId, addedAt)
                    SELECT old.playlistId, old.trackId, old.addedAt FROM playlist_tracks old
                    JOIN playlists ON old.playlistId = playlists.playlistId
                """)
                db.execSQL("DROP TABLE playlist_tracks")
                db.execSQL("ALTER TABLE playlist_tracks_new RENAME TO playlist_tracks")
                db.execSQL("CREATE INDEX index_playlist_tracks_trackId ON playlist_tracks (trackId)")
                db.execSQL("DROP TABLE favorite_tracks")
            }
        }
    }
}
