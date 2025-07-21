package com.example.recycleview.presentation.utils

import android.content.Context
import com.example.recycleview.R

fun formatDays(context: Context, text: String): String {
    if (text.isBlank()) return context.getString(R.string.days)

    val number = text.toIntOrNull() ?: return context.getString(R.string.days)
    val locale = context.resources.configuration.locales[0]

    return if (locale.language == "uk") {
        when {
            number % 10 == 1 && number % 100 != 11 -> context.getString(R.string.day) // день
            number % 10 in 2..4 && number % 100 !in 12..14 -> context.getString(R.string.days_2_3_4_uk) // дні
            else -> context.getString(R.string.days) // днів
        }
    } else {
        if (number == 1) context.getString(R.string.day) else context.getString(R.string.days)
    }
}

fun Int.days(context: Context): String {
    val locale = context.resources.configuration.locales[0]

    return if (locale.language == "uk") {
        val suffix = when {
            this % 10 == 1 && this % 100 != 11 -> context.getString(R.string.day) // день
            this % 10 in 2..4 && this % 100 !in 12..14 -> context.getString(R.string.days_2_3_4_uk) // дні
            else -> context.getString(R.string.days) // днів
        }
        "$this $suffix"
    } else {
        val suffix = if (this == 1) context.getString(R.string.day) else context.getString(R.string.days)
        "$this $suffix"
    }
}