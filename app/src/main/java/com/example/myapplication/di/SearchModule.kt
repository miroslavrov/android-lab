package com.example.myapplication.di

import androidx.room.Room
import com.example.myapplication.data.FavoritesRepositoryImpl
import com.example.myapplication.data.NetworkClientImpl
import com.example.myapplication.data.PlaylistsRepositoryImpl
import com.example.myapplication.data.SearchHistory
import com.example.myapplication.data.ItunesApi
import com.example.myapplication.data.TracksRepositoryImpl
import com.example.myapplication.data.db.AppDatabase
import com.example.myapplication.domain.FavoritesRepository
import com.example.myapplication.domain.NetworkClient
import com.example.myapplication.domain.PlaylistsRepository
import com.example.myapplication.domain.TrackHistory
import com.example.myapplication.domain.TracksRepository
import com.example.myapplication.ui.CreatePlaylistViewModel
import com.example.myapplication.ui.FavoritesViewModel
import com.example.myapplication.ui.PlayerViewModel
import com.example.myapplication.ui.PlaylistDetailsViewModel
import com.example.myapplication.ui.PlaylistsViewModel
import com.example.myapplication.ui.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal val searchModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "playlist_maker.db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().trackDao() }
    single<NetworkClient> { NetworkClientImpl(get()) }
    single<TracksRepository> { TracksRepositoryImpl(get(), get()) }
    single<TrackHistory> { SearchHistory(androidContext(), get()) }
    single<PlaylistsRepository> { PlaylistsRepositoryImpl(get()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { PlayerViewModel(get(), get(), get()) }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { CreatePlaylistViewModel(get()) }
    viewModel { PlaylistDetailsViewModel(get()) }
}
