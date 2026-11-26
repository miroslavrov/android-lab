package com.example.myapplication.data

import retrofit2.http.GET
import retrofit2.http.Query

internal interface ItunesApi {
    @GET("search?media=music&entity=song&limit=50")
    suspend fun search(@Query("term") query: String): ItunesSearchResponse
}

internal data class ItunesSearchResponse(val results: List<TrackDto>? = null)
