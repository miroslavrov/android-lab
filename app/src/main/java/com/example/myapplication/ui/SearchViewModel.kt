package com.example.myapplication.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.SearchServerException
import com.example.myapplication.domain.Track
import com.example.myapplication.domain.TrackHistory
import com.example.myapplication.domain.TracksRepository
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val tracksRepository: TracksRepository,
    private val trackHistory: TrackHistory,
    private val savedStateHandle: SavedStateHandle,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    val query = savedStateHandle.getStateFlow(SEARCH_QUERY, "")

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchState = _searchState.asStateFlow()

    private val _history = MutableStateFlow<List<Track>>(emptyList())
    val history = _history.asStateFlow()

    private var searchJob: Job? = null
    private var lastFailedQuery: String? = null

    init {
        viewModelScope.launch { _history.value = trackHistory.getHistory() }
    }

    fun updateQuery(value: String) {
        if (query.value == value) return

        savedStateHandle[SEARCH_QUERY] = value
        searchJob?.cancel()
        searchJob = null
        lastFailedQuery = null
        _searchState.value = SearchState.Initial
    }

    fun search() {
        val expression = query.value.trim()
        if (expression.isNotEmpty()) executeSearch(expression)
    }

    fun retry() {
        lastFailedQuery?.let(::executeSearch)
    }

    private fun executeSearch(expression: String) {
        searchJob?.cancel()
        lastFailedQuery = null
        _searchState.value = SearchState.Searching
        searchJob = viewModelScope.launch {
            try {
                val tracks = withContext(ioDispatcher) { tracksRepository.searchTracks(expression) }
                _searchState.value = SearchState.Success(tracks)
            } catch (error: IOException) {
                ensureActive()
                lastFailedQuery = expression
                _searchState.value = SearchState.Fail((error as? SearchServerException)?.statusCode)
            }
        }
    }

    fun rememberTrack(track: Track) {
        viewModelScope.launch {
            trackHistory.addTrack(track)
            _history.value = trackHistory.getHistory()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            trackHistory.clearHistory()
            _history.value = emptyList()
        }
    }

    private companion object {
        const val SEARCH_QUERY = "search_query"
    }
}
