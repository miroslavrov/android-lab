package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.Track
import com.example.myapplication.domain.TrackHistory
import com.example.myapplication.domain.TracksRepository
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val tracksRepository: TracksRepository,
    private val trackHistory: TrackHistory,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchState = _searchState.asStateFlow()

    private val _history = MutableStateFlow(trackHistory.getHistory())
    val history = _history.asStateFlow()

    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()
        val expression = query.trim()
        if (expression.isEmpty()) {
            resetSearch()
            return
        }

        _searchState.value = SearchState.Searching
        searchJob = viewModelScope.launch {
            try {
                val tracks = withContext(ioDispatcher) { tracksRepository.searchTracks(expression) }
                _searchState.value = SearchState.Success(tracks)
            } catch (_: IOException) {
                _searchState.value = SearchState.Fail
            }
        }
    }

    fun resetSearch() {
        searchJob?.cancel()
        searchJob = null
        _searchState.value = SearchState.Initial
    }

    fun rememberTrack(track: Track) {
        trackHistory.addTrack(track)
        _history.value = trackHistory.getHistory()
    }

    fun clearHistory() {
        trackHistory.clearHistory()
        _history.value = emptyList()
    }
}
