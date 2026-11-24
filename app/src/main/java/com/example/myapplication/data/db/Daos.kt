package com.example.myapplication.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("SELECT * FROM playlists ORDER BY name")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    suspend fun getPlaylist(playlistId: Long): PlaylistEntity?

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrack(playlistId: Long, trackId: String)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun deleteTracksOfPlaylist(playlistId: Long)

    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY addedAt DESC")
    fun getTracks(playlistId: Long): Flow<List<PlaylistTrackEntity>>

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    fun getTrackCount(playlistId: Long): Flow<Int>

    @Transaction
    suspend fun deletePlaylistWithTracks(playlistId: Long) {
        deleteTracksOfPlaylist(playlistId)
        deletePlaylist(playlistId)
    }

    @Transaction
    suspend fun insertTrackIntoPlaylist(track: PlaylistTrackEntity) {
        if (getPlaylist(track.playlistId) != null) {
            insertTrack(track)
        }
    }
}

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: FavoriteTrackEntity)

    @Query("DELETE FROM favorite_tracks WHERE trackId = :trackId")
    suspend fun delete(trackId: String)

    @Query("SELECT * FROM favorite_tracks ORDER BY addedAt DESC")
    fun getAll(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE trackId = :trackId)")
    fun isFavorite(trackId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE trackId = :trackId)")
    suspend fun contains(trackId: String): Boolean

    @Transaction
    suspend fun toggle(track: FavoriteTrackEntity) {
        if (contains(track.trackId)) {
            delete(track.trackId)
        } else {
            insert(track)
        }
    }
}
