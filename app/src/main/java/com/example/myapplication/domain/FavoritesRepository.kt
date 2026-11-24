package com.example.myapplication.domain

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavorites(): Flow<List<Track>>
    fun isFavorite(trackId: String): Flow<Boolean>
    suspend fun toggleFavorite(track: Track)
    suspend fun removeFavorite(trackId: String)
}
