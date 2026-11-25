package com.example.myapplication.ui

import com.example.myapplication.domain.Track

sealed interface SearchState {
    data object Initial : SearchState
    data object Searching : SearchState
    data class Success(val tracks: List<Track>) : SearchState
    data class Fail(val statusCode: Int? = null) : SearchState
}
