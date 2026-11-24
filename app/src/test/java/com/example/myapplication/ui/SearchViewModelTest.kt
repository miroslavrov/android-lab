package com.example.myapplication.ui

import androidx.lifecycle.SavedStateHandle
import com.example.myapplication.domain.Track
import com.example.myapplication.domain.TrackHistory
import com.example.myapplication.domain.TracksRepository
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val track = Track("1", "Smells Like Teen Spirit", "Nirvana", "05:01")

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun showsLoadingThenResults() = runTest(dispatcher) {
        val viewModel = newViewModel { listOf(track) }

        assertEquals(SearchState.Initial, viewModel.searchState.value)
        viewModel.updateQuery("Nirvana")
        viewModel.search()

        assertEquals(SearchState.Searching, viewModel.searchState.value)
        advanceUntilIdle()
        assertEquals(SearchState.Success(listOf(track)), viewModel.searchState.value)
    }

    @Test
    fun retryUsesTheSameQueryAfterAFailure() = runTest(dispatcher) {
        val queries = mutableListOf<String>()
        val viewModel = newViewModel { query ->
            queries.add(query)
            if (queries.size == 1) throw IOException()
            listOf(track)
        }

        viewModel.updateQuery(" Nirvana ")
        viewModel.search()
        advanceUntilIdle()
        assertEquals(SearchState.Fail, viewModel.searchState.value)

        viewModel.search()
        advanceUntilIdle()
        assertEquals(listOf("Nirvana", "Nirvana"), queries)
        assertEquals(SearchState.Success(listOf(track)), viewModel.searchState.value)
    }

    @Test
    fun clearingTheQueryCancelsTheInFlightSearch() = runTest(dispatcher) {
        val viewModel = newViewModel {
            delay(3000)
            listOf(track)
        }

        viewModel.updateQuery("Nirvana")
        viewModel.search()
        testScheduler.runCurrent()
        viewModel.updateQuery("")
        advanceUntilIdle()

        assertEquals("", viewModel.query.value)
        assertEquals(SearchState.Initial, viewModel.searchState.value)
    }

    @Test
    fun restoresQueryFromSavedState() {
        val savedState = SavedStateHandle()
        val firstViewModel = newViewModel(savedState) { emptyList() }
        firstViewModel.updateQuery("Nirvana")

        val recreatedViewModel = newViewModel(savedState) { emptyList() }

        assertEquals("Nirvana", recreatedViewModel.query.value)
        assertEquals(SearchState.Initial, recreatedViewModel.searchState.value)
    }

    private fun newViewModel(
        savedState: SavedStateHandle = SavedStateHandle(),
        search: suspend (String) -> List<Track>
    ): SearchViewModel {
        val repository = object : TracksRepository {
            override suspend fun searchTracks(query: String) = search(query)
        }
        val history = object : TrackHistory {
            override fun getHistory(): List<Track> = emptyList()
            override fun addTrack(track: Track) = Unit
            override fun clearHistory() = Unit
        }
        return SearchViewModel(repository, history, savedState, dispatcher)
    }
}
