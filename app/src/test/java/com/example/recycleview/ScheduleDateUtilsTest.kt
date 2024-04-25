package com.example.recycleview

import com.example.recycleview.utils.ScheduleDateUtils
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class ScheduleDateUtilsTest {

    @Test
    fun testCombineDateAndTime() {
        val dateInMillis = Instant.now().toEpochMilli()
        val timeInMillis = LocalTime.of(15, 0).toNanoOfDay() / 1_000_000 // Convert nanoseconds to milliseconds
        val combinedDateTimeInMillis = ScheduleDateUtils().combineDateAndTime(dateInMillis, timeInMillis)

        val date = Instant.ofEpochMilli(dateInMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val time = Instant.ofEpochMilli(timeInMillis).atZone(ZoneId.systemDefault()).toLocalTime()

        val expectedCombinedDateTime = LocalDateTime.of(date, time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        assertEquals(expectedCombinedDateTime, combinedDateTimeInMillis)
    }
}