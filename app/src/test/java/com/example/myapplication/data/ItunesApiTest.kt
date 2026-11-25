package com.example.myapplication.data

import com.example.myapplication.domain.SearchServerException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItunesApiTest {
    @Test
    fun reportsHttpFailureAndCanRepeatTheSameQuery() = runBlocking {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(503))
            server.enqueue(MockResponse().setBody("""
                {"results":[{"trackId":123,"trackName":"Song","artistName":"Artist","trackTimeMillis":65000}]}
            """.trimIndent()))
            val api = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ItunesApi::class.java)
            val client = NetworkClientImpl(api)

            val error = runCatching { client.searchTracks("rock & roll") }.exceptionOrNull()
            assertTrue(error is SearchServerException)
            assertEquals(503, (error as SearchServerException).statusCode)
            val track = client.searchTracks("rock & roll").single()
            assertEquals("123", track.trackId)
            assertEquals("01:05", track.trackTime)

            repeat(2) {
                val request = requireNotNull(server.takeRequest(5, TimeUnit.SECONDS))
                assertEquals("/search", request.requestUrl?.encodedPath)
                assertEquals("rock & roll", request.requestUrl?.queryParameter("term"))
            }
        }
    }
}
