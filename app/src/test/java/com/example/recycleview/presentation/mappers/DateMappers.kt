package com.example.recycleview.presentation.mappers

import com.example.recycleview.presentation.utils.mappers.calculateNextNotificationDate
import com.example.recycleview.presentation.utils.mappers.calculateNextNotificationDateLong
import com.example.recycleview.presentation.utils.mappers.daysToMillis
import com.example.recycleview.presentation.utils.mappers.localDateToMilliseconds
import com.example.recycleview.presentation.utils.mappers.localDateToString
import com.example.recycleview.presentation.utils.mappers.localDateToStringForUI
import com.example.recycleview.presentation.utils.mappers.localTimeToString
import com.example.recycleview.presentation.utils.mappers.millisToLocalDate
import com.example.recycleview.presentation.utils.mappers.parseLocalDate
import com.example.recycleview.presentation.utils.mappers.parseLocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale

class DateMappers {

    private lateinit var fixedDate: LocalDate
    private lateinit var fixedTime: LocalTime

    @Before
    fun setup() {
        // For stable formatting (EN)
        Locale.setDefault(Locale.UK)

        fixedDate = LocalDate.of(2024, 4, 1)
        fixedTime = LocalTime.of(10, 30)
    }

    @Test
    fun `millisToLocalDate converts correctly`() {
        val millis = localDateToMilliseconds(fixedDate)
        val result = millisToLocalDate(millis)
        assertEquals(fixedDate, result)
    }

    @Test
    fun `localDateToMilliseconds converts correctly`() {
        val millis = localDateToMilliseconds(fixedDate)
        val expected = fixedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        assertEquals(expected, millis)
    }

    @Test
    fun `localTimeToString formats correctly`() {
        val timeString = localTimeToString(fixedTime)
        assertEquals("10:30", timeString)
    }

    @Test
    fun `localDateToString returns formatted string in ROOT locale`() {
        val expected = "Monday, 01 April, 2024" // For Locale.ENGLISH
        val result = localDateToString(fixedDate)
        assertEquals(expected, result)
    }

    @Test
    fun `localDateToStringForUI returns formatted string in default locale`() {
        val expected = "Monday, 01 April, 2024" // depends on default locale set in @Before
        val result = localDateToStringForUI(fixedDate)
        assertEquals(expected, result)
    }

    @Test
    fun `parseLocalDate parses formatted date string`() {
        val expected = "Monday, 01 April, 2024"
        val parsed = parseLocalDate(expected)
        assertEquals(fixedDate, parsed)
    }

    @Test
    fun `parseLocalDate returns null on null input`() {
        val parsed = parseLocalDate(null)
        assertNull(parsed)
    }

    @Test
    fun `parseLocalTime parses formatted time string`() {
        val input = "10:30"
        val parsed = parseLocalTime(input)
        assertEquals(fixedTime, parsed)
    }

    @Test
    fun `parseLocalTime returns null on null input`() {
        val parsed = parseLocalTime(null)
        assertNull(parsed)
    }

    @Test
    fun `daysToMillis returns correct milliseconds`() {
        val days = 3
        val expected = 1000L * 60 * 60 * 24 * days
        val result = daysToMillis(days)
        assertEquals(expected, result)
    }

    @Test
    fun `calculateNextNotificationDate returns correct future date`() {
        val startDate = "Monday, 14 April, 2025"
        val startTime = "10:00"
        val interval = 3L

        val result = calculateNextNotificationDate(startDate, startTime, interval)
        val parsed = parseLocalDate(result)

        assertNotNull(parsed)
        assertTrue(parsed!!.isAfter(LocalDate.now()) || parsed == LocalDate.now())
    }

    @Test
    fun `calculateNextNotificationDateLong returns correct future millis`() {
        val startDate = LocalDate.of(2024, 4, 1)
        val startTime = LocalTime.of(10, 0)
        val interval = 5L

        val resultMillis = calculateNextNotificationDateLong(startDate, startTime, interval)
        val resultDate = Instant.ofEpochMilli(resultMillis).atZone(ZoneId.systemDefault()).toLocalDate()

        assertTrue(resultDate.isAfter(LocalDate.now()) || resultDate == LocalDate.now())
    }
}