package com.example.myapplication.domain

import java.io.IOException

fun interface NetworkClient {
    suspend fun searchTracks(query: String): List<Track>
}

class SearchServerException(val statusCode: Int) : IOException("Search failed with HTTP $statusCode")
