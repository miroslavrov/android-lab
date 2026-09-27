package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.myapplication.ui.MainScreen
import com.example.myapplication.ui.MediaLibraryScreen
import com.example.myapplication.ui.Screen
import com.example.myapplication.ui.SearchScreen
import com.example.myapplication.ui.SettingsScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDark) }
            var currentScreen by remember { mutableStateOf(Screen.MAIN) }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                when (currentScreen) {
                    Screen.MAIN -> MainScreen(
                        onNavigate = { screen -> currentScreen = screen }
                    )
                    Screen.SEARCH -> SearchScreen(
                        onBack = { currentScreen = Screen.MAIN }
                    )
                    Screen.MEDIA_LIBRARY -> MediaLibraryScreen(
                        onBack = { currentScreen = Screen.MAIN }
                    )
                    Screen.SETTINGS -> SettingsScreen(
                        isDarkTheme = isDarkTheme,
                        onDarkThemeChange = { isDarkTheme = it },
                        onBack = { currentScreen = Screen.MAIN }
                    )
                }
            }
        }
    }
}
