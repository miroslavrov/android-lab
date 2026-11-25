package com.example.myapplication.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("SELECT * FROM playlists ORDER BY name")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    suspend fun getPlaylist(playlistId: Long): PlaylistEntity?

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrack(playlistId: Long, trackId: String)

    @Query("""
        SELECT t.* FROM tracks t JOIN playlist_tracks p ON p.trackId = t.trackId
        WHERE p.playlistId = :playlistId ORDER BY p.addedAt DESC
    """)
    fun getTracks(playlistId: Long): Flow<List<TrackEntity>>

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    fun getTrackCount(playlistId: Long): Flow<Int>

    @Transaction
    suspend fun insertTrackIntoPlaylist(track: PlaylistTrackEntity) {
        if (getPlaylist(track.playlistId) != null) insertTrack(track)
    }

    @Query("""
        INSERT OR IGNORE INTO playlist_tracks (playlistId, trackId, addedAt)
        SELECT :targetId, trackId, addedAt FROM playlist_tracks WHERE playlistId = :sourceId
    """)
    suspend fun copyTracks(sourceId: Long, targetId: Long)

    @Transaction
    suspend fun mergePlaylists(sourceId: Long, targetId: Long) {
        if (sourceId == targetId || getPlaylist(targetId) == null) return
        copyTracks(sourceId, targetId)
        deletePlaylist(sourceId)
    }
}

@Dao
interface TrackDao {
    @Upsert(entity = TrackEntity::class)
    suspend fun cache(tracks: List<TrackMetadata>)

    @Query("SELECT * FROM tracks WHERE trackId = :trackId")
    suspend fun find(trackId: String): TrackEntity?

    @Query("SELECT * FROM tracks WHERE trackId IN (:ids)")
    suspend fun findAll(ids: List<String>): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE isFavorite = 1 ORDER BY favoriteAddedAt DESC")
    fun getFavorites(): Flow<List<TrackEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM tracks WHERE trackId = :trackId AND isFavorite = 1)")
    fun isFavorite(trackId: String): Flow<Boolean>

    @Query("""
        UPDATE tracks SET
            favoriteAddedAt = CASE WHEN isFavorite = 1 THEN NULL ELSE :timestamp END,
            isFavorite = NOT isFavorite
        WHERE trackId = :trackId
    """)
    suspend fun toggleFavorite(trackId: String, timestamp: Long)

    @Query("UPDATE tracks SET isFavorite = 0, favoriteAddedAt = NULL WHERE trackId = :trackId")
    suspend fun removeFavorite(trackId: String)
}
