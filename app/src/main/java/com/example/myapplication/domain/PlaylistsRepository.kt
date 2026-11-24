package com.example.myapplication.domain

import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(name: String, description: String): Long
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun getPlaylist(playlistId: Long): Playlist?
    fun getPlaylistTracks(playlistId: Long): Flow<List<Track>>
    suspend fun addTrack(playlistId: Long, track: Track)
    suspend fun removeTrack(playlistId: Long, trackId: String)
}
