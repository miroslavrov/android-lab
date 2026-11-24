package com.example.myapplication.data

import com.example.myapplication.data.db.FavoriteDao
import com.example.myapplication.data.db.FavoriteTrackEntity
import com.example.myapplication.domain.FavoritesRepository
import com.example.myapplication.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class FavoritesRepositoryImpl(
    private val dao: FavoriteDao
) : FavoritesRepository {

    override fun getFavorites(): Flow<List<Track>> =
        dao.getAll().map { list -> list.map { it.toTrack() } }

    override fun isFavorite(trackId: String): Flow<Boolean> = dao.isFavorite(trackId)

    override suspend fun toggleFavorite(track: Track) {
        if (dao.isFavorite(track.trackId).first()) {
            dao.delete(track.trackId)
        } else {
            dao.insert(track.toEntity())
        }
    }

    override suspend fun removeFavorite(trackId: String) {
        dao.delete(trackId)
    }

    private fun FavoriteTrackEntity.toTrack() = Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl = artworkUrl,
        previewUrl = previewUrl
    )

    private fun Track.toEntity() = FavoriteTrackEntity(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl = artworkUrl,
        previewUrl = previewUrl
    )
}
