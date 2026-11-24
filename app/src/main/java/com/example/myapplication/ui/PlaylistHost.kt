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
            SearchScreen(onBack = onBack)
        }
        composable(Screen.MEDIA_LIBRARY.name) {
            MediaLibraryScreen(onBack = onBack)
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
