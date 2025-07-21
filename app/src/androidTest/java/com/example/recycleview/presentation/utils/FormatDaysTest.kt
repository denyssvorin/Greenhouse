package com.example.recycleview.presentation.utils

import android.content.Context
import android.content.res.Configuration
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

class FormatDaysTest {

    private lateinit var context: Context
    private lateinit var originalLocale: Locale

    @Before
    fun setup() {
        val providerContext = ApplicationProvider.getApplicationContext<Context>()

        // Save the current locale
        originalLocale = providerContext.resources.configuration.locales[0]

        // Set the locale to English
        val config = Configuration(providerContext.resources.configuration)
        config.setLocale(Locale("en"))
        context = providerContext.createConfigurationContext(config)
    }

    @Test
    fun formatDays_returnsCorrectSuffixFor1() {
        val result = formatDays(context, "1")
        assertEquals("day", result)
    }

    @Test
    fun formatDays_returnsCorrectSuffixFor3() {
        val result = formatDays(context, "3")
        assertEquals("days", result)
    }

    @Test
    fun formatDays_returnsCorrectSuffixFor5() {
        val result = formatDays(context, "5")
        assertEquals("days", result)
    }

    @Test
    fun formatDays_returnsDefaultWhenTextIsBlank() {
        val result = formatDays(context, "")
        assertEquals("days", result)
    }

    @Test
    fun intDaysExtensionWorksCorrectlyFor21() {
        val result = 21.days(context)
        assertEquals("21 days", result)
    }

    @Test
    fun intDaysExtensionWorksCorrectlyFor24() {
        val result = 24.days(context)
        assertEquals("24 days", result)
    }

    @Test
    fun intDaysExtensionWorksCorrectlyFor25() {
        val result = 25.days(context)
        assertEquals("25 days", result)
    }

    @Test
    fun intDaysExtensionWorksCorrectlyFor11() {
        val result = 11.days(context)
        assertEquals("11 days", result)
    }

    @After
    fun restoreOriginalLocale() {
        // Restore the system locale
        Locale.setDefault(originalLocale)

        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val resources = appContext.resources
        val config = resources.configuration
        config.setLocale(originalLocale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}