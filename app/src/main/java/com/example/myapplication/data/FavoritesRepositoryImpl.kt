package com.example.myapplication.data

import com.example.myapplication.data.db.TrackDao
import com.example.myapplication.domain.FavoritesRepository
import com.example.myapplication.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FavoritesRepositoryImpl(private val dao: TrackDao) : FavoritesRepository {
    override fun getFavorites(): Flow<List<Track>> = dao.getFavorites().map { tracks ->
        tracks.map { it.toTrack() }
    }

    override fun isFavorite(trackId: String): Flow<Boolean> = dao.isFavorite(trackId)

    override suspend fun toggleFavorite(track: Track) {
        dao.toggleFavorite(track.trackId, System.currentTimeMillis())
    }

    override suspend fun removeFavorite(trackId: String) {
        dao.removeFavorite(trackId)
    }
}
