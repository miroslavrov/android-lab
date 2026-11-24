package com.example.myapplication.data

internal class Storage {
    val tracks: List<TrackDto> = listOf(
        TrackDto(
            trackId = "1",
            trackName = "Smells Like Teen Spirit",
            artistName = "Nirvana",
            trackTimeMillis = 301000,
            artworkUrl = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-435572db86cc/00602537992281.rgb.jpg/100x100bb.jpg"
        ),
        TrackDto(
            trackId = "2",
            trackName = "Billie Jean",
            artistName = "Michael Jackson",
            trackTimeMillis = 294000,
            artworkUrl = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/886443546264.jpg/100x100bb.jpg"
        ),
        TrackDto(
            trackId = "3",
            trackName = "Stayin' Alive",
            artistName = "Bee Gees",
            trackTimeMillis = 250000,
            artworkUrl = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801f3b-3832-3402-236b-d36a715f5c22/00602537869770.rgb.jpg/100x100bb.jpg"
        ),
        TrackDto(
            trackId = "4",
            trackName = "Whole Lotta Love",
            artistName = "Led Zeppelin",
            trackTimeMillis = 333000,
            artworkUrl = "https://is2-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/b4/a04db484-1181-2da2-04e8-8b9a1f2f87a8/886444588263.jpg/100x100bb.jpg"
        ),
        TrackDto(
            trackId = "5",
            trackName = "Sweet Child O'Mine",
            artistName = "Guns N' Roses",
            trackTimeMillis = 303000,
            artworkUrl = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/b4/a04db484-1181-2da2-04e8-8b9a1f2f87a8/886444588263.jpg/100x100bb.jpg"
        )
    )
}
