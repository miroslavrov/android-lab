package com.example.myapplication.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaylistTrackCountTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun keepsRussianTrackCountsInRussianAndEnglishConfigurations() {
        val counts = listOf(0, 1, 2, 5, 11, 21, 22)
        compose.setContent {
            val context = LocalContext.current
            val systemConfiguration = LocalConfiguration.current
            Column {
                for (locale in listOf(Locale.forLanguageTag("ru"), Locale.ENGLISH)) {
                    val configuration = Configuration(systemConfiguration).apply {
                        setLocale(locale)
                    }
                    CompositionLocalProvider(
                        LocalContext provides context.createConfigurationContext(configuration),
                        LocalConfiguration provides configuration
                    ) {
                        for (count in counts) {
                            Text(playlistTrackCountText(count))
                        }
                    }
                }
            }
        }

        for (text in listOf("0 треков", "1 трек", "2 трека", "5 треков", "11 треков", "21 трек", "22 трека")) {
            compose.onAllNodesWithText(text).assertCountEquals(2)
        }
    }
}
