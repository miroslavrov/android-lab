package com.example.myapplication.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun PlaylistHost(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    val onBack: () -> Unit = { navController.popBackStack() }

    NavHost(navController = navController, startDestination = Screen.MAIN.name) {
        composable(Screen.MAIN.name) {
            MainScreen { screen ->
                navController.navigate(screen.name) { launchSingleTop = true }
            }
        }
        composable(Screen.SEARCH.name) {
            SearchScreen(
                onBack = onBack,
                isDark = isDarkTheme,
                onOpenPlayer = { track -> navController.navigate("${Screen.PLAYER.name}/${track.trackId}") }
            )
        }
        composable("${Screen.PLAYER.name}/{trackId}") { entry ->
            PlayerScreen(
                trackId = entry.arguments?.getString("trackId").orEmpty(),
                onBack = onBack,
                isDark = isDarkTheme
            )
        }
        composable(Screen.MEDIA_LIBRARY.name) {
            MediaLibraryScreen(
                onBack = onBack,
                isDark = isDarkTheme,
                onOpenPlayer = { track -> navController.navigate("${Screen.PLAYER.name}/${track.trackId}") },
                onOpenPlaylist = { id -> navController.navigate("${Screen.PLAYLIST_DETAILS.name}/$id") },
                onCreatePlaylist = { navController.navigate(Screen.CREATE_PLAYLIST.name) }
            )
        }
        composable(Screen.CREATE_PLAYLIST.name) {
            CreatePlaylistScreen(onBack = onBack)
        }
        composable("${Screen.PLAYLIST_DETAILS.name}/{playlistId}") { entry ->
            PlaylistDetailsScreen(
                playlistId = entry.arguments?.getString("playlistId")?.toLongOrNull() ?: 0L,
                onBack = onBack,
                isDark = isDarkTheme
            )
        }
        composable(Screen.SETTINGS.name) {
            SettingsScreen(
                isDarkTheme = isDarkTheme,
                onDarkThemeChange = onDarkThemeChange,
                onBack = onBack
            )
        }
    }
}
