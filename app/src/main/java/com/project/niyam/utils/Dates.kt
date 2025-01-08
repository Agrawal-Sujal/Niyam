package com.project.niyam.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

enum class DateTimeDetail {
    FULL_DAY_NAME,
    SHORT_DAY_NAME,
    FULL_MONTH_NAME,
    SHORT_MONTH_NAME,
    FULL_YEAR,
    SHORT_YEAR,
    DATE,
    FULL_DATE,
    HOUR_24,
    HOUR_12,
    MINUTE,
    AM_PM,
    ;

    fun getDetail(): String {
        val calendar = Calendar.getInstance()
        val format = when (this) {
            FULL_DAY_NAME -> "EEEE"
            SHORT_DAY_NAME -> "EEE"
            FULL_MONTH_NAME -> "MMMM"
            SHORT_MONTH_NAME -> "MMM"
            FULL_YEAR -> "yyyy"
            SHORT_YEAR -> "yy"
            DATE -> "d"
            FULL_DATE -> "d MMMM yyyy"
            HOUR_24 -> "HH"
            HOUR_12 -> "hh"
            MINUTE -> "mm"
            AM_PM -> "a"
        }
        return SimpleDateFormat(format, Locale.getDefault()).format(calendar.time)
    }
}

enum class DateDetail {
    FULL_DAY_NAME,
    SHORT_DAY_NAME,
    FULL_MONTH_NAME,
    SHORT_MONTH_NAME,
    FULL_YEAR,
    DATE,
    FULL_DATE,
    ;

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDetail(localDate: LocalDate): String {
        val formatter = when (this) {
            FULL_DAY_NAME -> DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
            SHORT_DAY_NAME -> DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
            FULL_MONTH_NAME -> DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
            SHORT_MONTH_NAME -> DateTimeFormatter.ofPattern("MMM", Locale.getDefault())
            FULL_YEAR -> DateTimeFormatter.ofPattern("yyyy", Locale.getDefault())
            DATE -> DateTimeFormatter.ofPattern("d", Locale.getDefault())
            FULL_DATE -> DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
        }
        return localDate.format(formatter)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertToLocalTime(hardCodedTime: String): String {
    // Define the format of the hard-coded time string (for example, "HH:mm")
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    // Parse the hard-coded time into LocalTime (just time, no date)
    val localTime = LocalTime.parse(hardCodedTime, formatter)

    // Convert to ZonedDateTime using the system's default timezone (to reflect local time)
    val systemTimeZone = ZoneId.systemDefault()
    val zonedDateTime = localTime.atDate(LocalDate.now()).atZone(systemTimeZone)

    // Extract the local time (hour and minute)
    val localTimeAdjusted = zonedDateTime.toLocalTime()

    // Return the formatted time in a string (you can modify the format as required)
    return localTimeAdjusted.format(DateTimeFormatter.ofPattern("HH:mm"))
}
