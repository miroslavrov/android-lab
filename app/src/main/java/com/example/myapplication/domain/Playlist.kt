package com.example.myapplication.domain

data class Playlist(
    val playlistId: Long,
    val name: String,
    val description: String,
    val trackCount: Int = 0
)
