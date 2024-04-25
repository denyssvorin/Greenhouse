package com.example.recycleview.utils

fun formatDays(text: String): String {
    return if (text.equals("1")) {
        "day"
    } else {
        "days"
    }
}
fun Int.days(): String {
    return if (this == 1) {
        "$this day"
    } else {
        "$this days"
    }
}