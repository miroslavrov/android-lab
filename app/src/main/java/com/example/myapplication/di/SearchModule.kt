package com.example.myapplication.di

import com.example.myapplication.data.SearchHistory
import com.example.myapplication.data.Storage
import com.example.myapplication.data.TracksRepositoryImpl
import com.example.myapplication.domain.TrackHistory
import com.example.myapplication.domain.TracksRepository
import com.example.myapplication.ui.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val searchModule = module {
    single { Storage() }
    single<TracksRepository> { TracksRepositoryImpl(get()) }
    single<TrackHistory> { SearchHistory(androidContext(), get()) }
    viewModel { SearchViewModel(get(), get()) }
}
