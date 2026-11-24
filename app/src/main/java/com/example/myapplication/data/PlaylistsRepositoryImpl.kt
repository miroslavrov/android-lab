package com.example.myapplication.data

import com.example.myapplication.data.db.PlaylistDao
import com.example.myapplication.data.db.PlaylistEntity
import com.example.myapplication.data.db.PlaylistTrackEntity
import com.example.myapplication.domain.Playlist
import com.example.myapplication.domain.PlaylistsRepository
import com.example.myapplication.domain.Track
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
internal class PlaylistsRepositoryImpl(
    private val dao: PlaylistDao
) : PlaylistsRepository {

    override fun getPlaylists(): Flow<List<Playlist>> =
        dao.getPlaylists().flatMapLatest { entities ->
            if (entities.isEmpty()) return@flatMapLatest flowOf(emptyList())
            combine(
                entities.map { entity ->
                    dao.getTrackCount(entity.playlistId).map { count ->
                        entity.toPlaylist(count)
                    }
                }
            ) { it.toList() }
        }

    override suspend fun createPlaylist(name: String, description: String): Long =
        dao.insertPlaylist(PlaylistEntity(name = name, description = description))

    override suspend fun deletePlaylist(playlistId: Long) {
        dao.deletePlaylistWithTracks(playlistId)
    }

    override suspend fun getPlaylist(playlistId: Long): Playlist? =
        dao.getPlaylist(playlistId)?.toPlaylist(0)

    override fun getPlaylistTracks(playlistId: Long): Flow<List<Track>> =
        dao.getTracks(playlistId).map { list -> list.map { it.toTrack() } }

    override suspend fun addTrack(playlistId: Long, track: Track) {
        dao.insertTrackIntoPlaylist(track.toPlaylistTrackEntity(playlistId))
    }

    override suspend fun removeTrack(playlistId: Long, trackId: String) {
        dao.deleteTrack(playlistId, trackId)
    }

    override suspend fun mergePlaylists(sourceId: Long, targetId: Long) {
        dao.mergePlaylists(sourceId, targetId)
    }

    private fun PlaylistEntity.toPlaylist(trackCount: Int) = Playlist(
        playlistId = playlistId,
        name = name,
        description = description,
        trackCount = trackCount
    )

    private fun PlaylistTrackEntity.toTrack() = Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl = artworkUrl,
        previewUrl = previewUrl
    )

    private fun Track.toPlaylistTrackEntity(playlistId: Long) = PlaylistTrackEntity(
        playlistId = playlistId,
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl = artworkUrl,
        previewUrl = previewUrl
    )
}
