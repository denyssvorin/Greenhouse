package com.example.recycleview.presentation.utils

import android.content.Context
import com.example.recycleview.R

fun formatDays(context: Context, text: String): String {
    return if (text.isNotEmpty() || text.isNotBlank()) {
        val number = text.toInt()
        val suffix = when {
            number % 10 == 1 && number % 100 != 11 -> context.getString(R.string.day)
            number % 10 in 2..4 && number % 100 !in 12..14 -> context.getString(R.string.days_2_3_4_uk)
            else -> context.getString(R.string.days)
        }
        suffix
    } else {
        context.getString(R.string.days)
    }

}

fun Int.days(context: Context): String {
    val suffix = when {
        this % 10 == 1 && this % 100 != 11 -> context.getString(R.string.day)
        this % 10 in 2..4 && this % 100 !in 12..14 -> context.getString(R.string.days_2_3_4_uk)
        else -> context.getString(R.string.days)
    }
    return "$this $suffix"
}