package com.example.myapplication.data

import android.net.ConnectivityManager
import com.example.myapplication.domain.NetworkClient

internal class NetworkClientImpl(
    private val connectivity: ConnectivityManager?
) : NetworkClient {
    override fun isConnected(): Boolean = connectivity?.activeNetwork != null
}
