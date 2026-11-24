package com.example.myapplication.data

import com.example.myapplication.domain.NetworkClient
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TracksRepositoryTest {
    @Test
    fun opensTrackByIdWithoutHistoryOrConnectivity() {
        val repository = TracksRepositoryImpl(Storage(), NetworkClient { false })

        assertEquals("Smells Like Teen Spirit", repository.getTrack("1")?.trackName)
        assertNull(repository.getTrack("unknown"))
    }

    @Test
    fun matchesArtistAndTrackNameIgnoringCase() = runTest {
        val repository = TracksRepositoryImpl(Storage(), NetworkClient { true })

        val byArtist = repository.searchTracks("  nIrVaNa  ").single()
        val byTitle = repository.searchTracks("TEEN SPIRIT").single()

        assertEquals("Smells Like Teen Spirit", byArtist.trackName)
        assertEquals(byArtist, byTitle)
        assertEquals("05:01", byTitle.trackTime)
    }

    @Test
    fun returnsEmptyListForBlankAndUnmatchedQueries() = runTest {
        val repository = TracksRepositoryImpl(Storage(), NetworkClient { true })

        assertTrue(repository.searchTracks("  ").isEmpty())
        assertTrue(repository.searchTracks("unknown track").isEmpty())
    }

    @Test
    fun reportsAnIoFailureWhenOffline() = runTest {
        val repository = TracksRepositoryImpl(Storage(), NetworkClient { false })

        val result = runCatching { repository.searchTracks("Nirvana") }

        assertTrue(result.exceptionOrNull() is IOException)
    }
}
