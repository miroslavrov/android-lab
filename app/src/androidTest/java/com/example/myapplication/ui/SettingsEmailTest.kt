package com.example.myapplication.ui

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.MailTo
import android.os.Build
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.example.myapplication.R
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
class SettingsEmailTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun contactDeveloperOpensAnAddressedDraft() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val configuredEmail = context.getString(R.string.developer_email).trim()
        val expectedEmail = InstrumentationRegistry.getArguments()
            .getString("expectedDeveloperEmail", configuredEmail.ifEmpty { "playlistmaker@example.com" })
        val sentIntent = AtomicReference<Intent>()
        val monitor = object : Instrumentation.ActivityMonitor() {
            override fun onStartActivity(intent: Intent): Instrumentation.ActivityResult? {
                if (intent.action != Intent.ACTION_SENDTO || intent.scheme != "mailto") return null
                sentIntent.set(intent)
                return Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null)
            }
        }
        instrumentation.addMonitor(monitor)
        try {
            compose.setContent {
                SettingsScreen(isDarkTheme = false, onDarkThemeChange = {}, onBack = {})
            }
            compose.onNodeWithText(context.getString(R.string.settings_contact_developer)).performClick()

            val draft = MailTo.parse(requireNotNull(sentIntent.get()?.dataString))
            assertEquals(expectedEmail, draft.to)
            assertEquals(context.getString(R.string.settings_email_subject), draft.subject)
            assertEquals(context.getString(R.string.settings_email_body), draft.body)
        } finally {
            instrumentation.removeMonitor(monitor)
        }
    }
}
