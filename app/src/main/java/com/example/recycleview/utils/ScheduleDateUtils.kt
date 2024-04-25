package com.example.recycleview.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class ScheduleDateUtils {

    fun millisToLocalDate(millis: Long): LocalDate {
        return Instant
            .ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    private fun millisToLocalDateWithFormatter(
        date: LocalDate,
        dateTimeFormatter: DateTimeFormatter
    ): LocalDate {
        //Convert the date to a long in millis using a dateformmater
        val dateInMillis = LocalDate.parse(date.format(dateTimeFormatter), dateTimeFormatter)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        //Convert the millis to a localDate object
        return Instant
            .ofEpochMilli(dateInMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }


    fun localDateToString(date: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.getDefault())
        val dateInMillis = millisToLocalDateWithFormatter(date, dateFormatter)
        return dateFormatter.format(dateInMillis)
    }

    fun localDateToMilliseconds(localDate: LocalDate): Long {
        val startOfDay = localDate.atStartOfDay(ZoneOffset.UTC)
        return startOfDay.toInstant().toEpochMilli()
    }

    fun localTimeToMilliseconds(localTime: LocalTime): Long {
        val startOfDay = LocalDate.now().atTime(localTime)
        return startOfDay.toInstant(ZoneId.systemDefault().rules.getOffset(Instant.now()))
            .toEpochMilli()
    }


    fun localTimeToString(localTime: LocalTime): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return formatter.format(localTime)
    }

    fun combineDateAndTime(dateInMillis: Long, timeInMillis: Long): Long {
        val date = Instant.ofEpochMilli(dateInMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val time = Instant.ofEpochMilli(timeInMillis).atZone(ZoneId.systemDefault()).toLocalTime()
        val combinedDateTime = LocalDateTime.of(date, time)
        return combinedDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun daysToMillis(days: Int): Long = 1000L * 60 * 60 * 24 * days

    fun calculateNextNotificationDate(
        startDate: String,
        notificationTime: String,
        interval: Long
    ): String {
        var nextNotificationDate: LocalDate = parseLocalDate(startDate)
        val nextNotificationTime: LocalTime = parseLocalTime(notificationTime)

        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()

        while (nextNotificationDate.isBefore(currentDate)
            || (nextNotificationDate == currentDate && currentTime > nextNotificationTime)
        ) {
            nextNotificationDate = nextNotificationDate.plusDays(interval)
        }
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy")
        return nextNotificationDate.format(formatter)
    }

    private fun parseLocalTime(timeString: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.parse(timeString, formatter)
    }

    private fun parseLocalDate(dateString: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.getDefault())
        return LocalDate.parse(dateString, formatter)
    }
}
