package com.example.myapplication.data

import com.example.myapplication.data.db.TrackEntity
import com.example.myapplication.data.db.TrackMetadata
import com.example.myapplication.domain.Track

internal fun TrackEntity.toTrack() = Track(
    trackId, trackName, artistName, trackTime, artworkUrl, previewUrl, isFavorite
)

internal fun Track.toMetadata() = TrackMetadata(
    trackId, trackName, artistName, trackTime, artworkUrl, previewUrl
)
