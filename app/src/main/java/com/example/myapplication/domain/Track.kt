package com.example.myapplication.domain

data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl: String = "",
    val previewUrl: String = "",
    val isFavorite: Boolean = false
)
