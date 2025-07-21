package com.example.recycleview.data.scheduler.utils

import com.example.recycleview.presentation.utils.mappers.parseLocalDate
import com.example.recycleview.presentation.utils.mappers.parseLocalTime
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateMappersTest {

    @Test
    fun `parseLocalDate should parse correct format`() {
        val input = "Monday, 01 January, 2024"
        val expected = LocalDate.of(2024, 1, 1)
        val actual = parseLocalDate(input)
        assertEquals(expected, actual)
    }

    @Test
    fun `parseLocalTime should parse correct format`() {
        val input = "14:30"
        val expected = LocalTime.of(14, 30)
        val actual = parseLocalTime(input)
        assertEquals(expected, actual)
    }

    @Test
    fun `calculateNextNotificationDateLong returns the same date if future time today`() {
        val today = LocalDate.now()
        val formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.ENGLISH))

        val futureTime = LocalTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm"))
        val result = calculateNextNotificationDateLong(
            startDate = formattedDate,
            notificationTime = futureTime,
            interval = 3L
        )

        val expected = today.atTime(LocalTime.parse(futureTime)).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertEquals(expected, result)
    }

    @Test
    fun `calculateNextNotificationDateLong skips to next interval if time already passed today`() {
        val today = LocalDate.now()
        val formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.ENGLISH))

        val pastTime = LocalTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("HH:mm"))
        val interval = 2L

        val result = calculateNextNotificationDateLong(
            startDate = formattedDate,
            notificationTime = pastTime,
            interval = interval
        )

        val expectedDate = today.plusDays(interval)
        val expected = expectedDate.atTime(LocalTime.parse(pastTime))
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `calculateNextNotificationDateLong skips multiple intervals if date is far in the past`() {
        val startDate = LocalDate.now().minusDays(10)
        val formattedDate = startDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.ENGLISH))
        val time = "10:00"
        val interval = 3L

        val result = calculateNextNotificationDateLong(
            startDate = formattedDate,
            notificationTime = time,
            interval = interval
        )

        var nextDate = startDate
        val now = LocalDate.now()
        val nowTime = LocalTime.now()
        val notificationTime = parseLocalTime(time)

        while (nextDate.isBefore(now) || (nextDate == now && nowTime > notificationTime)) {
            nextDate = nextDate.plusDays(interval)
        }

        val expected = nextDate.atTime(notificationTime)
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `daysToMillis should convert days correctly`() {
        val days = 2
        val expectedMillis = 2L * 24 * 60 * 60 * 1000
        val actual = daysToMillis(days)
        assertEquals(expectedMillis, actual)
    }
}