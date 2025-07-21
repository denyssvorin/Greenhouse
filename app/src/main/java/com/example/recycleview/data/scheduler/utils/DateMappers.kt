package com.example.recycleview.data.scheduler.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun daysToMillis(days: Int): Long = 1000L * 60 * 60 * 24 * days

fun calculateNextNotificationDateLong(
    startDate: String,
    notificationTime: String,
    interval: Long
): Long {
    var nextNotificationDate: LocalDate = parseLocalDate(startDate)
    val nextNotificationTime: LocalTime = parseLocalTime(notificationTime)

    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()

    while (nextNotificationDate.isBefore(currentDate)
        || (nextNotificationDate == currentDate && currentTime > nextNotificationTime)
    ) {
        nextNotificationDate = nextNotificationDate.plusDays(interval)
    }

    val nextNotificationDateTime = LocalDateTime.of(nextNotificationDate, nextNotificationTime)
    val longValue = nextNotificationDateTime
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    return longValue
}

private fun parseLocalTime(timeString: String): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return LocalTime.parse(timeString, formatter)
}

private fun parseLocalDate(dateString: String): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.ENGLISH)
    return LocalDate.parse(dateString, formatter)
}