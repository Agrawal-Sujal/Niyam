package com.project.niyam.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@SuppressLint("DefaultLocale")
fun Int.toHms(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun String.toSeconds(): Int {
    val parts = split(":").map { it.toIntOrNull() ?: 0 }
    return when (parts.size) {
        3 -> parts[0] * 3600 + parts[1] * 60 + parts[2]
        2 -> parts[0] * 60 + parts[1]
        else -> 0
    }
}



@RequiresApi(Build.VERSION_CODES.O)
fun secondsUntil(endDate: LocalDate, endTime: LocalTime): Long {

    val date = endDate
    val time = endTime

    val endDateTime = LocalDateTime.of(date, time)

    // Current time in same zone
    val now = LocalDateTime.now(ZoneId.systemDefault())

    // Calculate seconds difference
    return ChronoUnit.SECONDS.between(now, endDateTime)
}