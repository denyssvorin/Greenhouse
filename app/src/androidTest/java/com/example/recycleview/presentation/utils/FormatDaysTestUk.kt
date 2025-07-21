package com.example.recycleview.presentation.utils

import android.content.Context
import android.content.res.Configuration
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Locale

class FormatDaysTestUk {

    private lateinit var context: Context
    private lateinit var originalLocale: Locale

    @Before
    fun setup() {
        val providerContext = ApplicationProvider.getApplicationContext<Context>()

        // Save the current locale
        originalLocale = providerContext.resources.configuration.locales[0]

        // Set the locale to English
        val config = Configuration(providerContext.resources.configuration)
        config.setLocale(Locale("uk"))
        context = providerContext.createConfigurationContext(config)
    }

    @Test
    fun formatDays_returnsCorrectSuffixFor1() {
        val result = formatDays(context, "1")
        Assert.assertEquals("день", result)
    }

    @Test
    fun formatDays_returnsCorrectSuffixFor3() {
        val result = formatDays(context, "3")
        Assert.assertEquals("дні", result)
    }

    @Test
    fun formatDays_returnsCorrectSuffixFor5() {
        val result = formatDays(context, "5")
        Assert.assertEquals("днів", result)
    }

    @Test
    fun formatDays_returnsDefaultWhenTextIsBlank() {
        val result = formatDays(context, "")
        Assert.assertEquals("днів", result)
    }

    @Test
    fun intDaysExtensionWorksCorrectlyFor21() {
        val result = 21.days(context)
        Assert.assertEquals("21 день", result)
    }

    @Test
    fun intDaysExtensionWorksCorrectlyFor24() {
        val result = 24.days(context)
        Assert.assertEquals("24 дні", result)
    }

    @Test
    fun intDaysExtensionWorksCorrectlyFor25() {
        val result = 25.days(context)
        Assert.assertEquals("25 днів", result)
    }

    @Test
    fun intDaysExtensionWorksCorrectlyFor11() {
        val result = 11.days(context)
        Assert.assertEquals("11 днів", result)
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
