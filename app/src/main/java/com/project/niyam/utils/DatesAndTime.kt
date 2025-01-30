package com.project.niyam.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
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
            FULL_DATE -> "yyyy-MM-DD"
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
            FULL_DATE -> DateTimeFormatter.ofPattern("yyyy-MM-DD", Locale.getDefault())
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

@RequiresApi(Build.VERSION_CODES.O)
fun daysRemaining(targetDate: String): Long {
    try {
        // Define the date formatter for "dd-MM-yyyy" format
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-DD")

        // Parse the target date
        val target = LocalDate.parse(targetDate, formatter)

        // Get today's date
        val today = LocalDate.now()

        // Calculate the difference in days
        return ChronoUnit.DAYS.between(today, target) + 1L
    } catch (e: Exception) {
        println("Invalid date format. Please use 'dd-MM-yyyy'. Error: ${e.message}")
        return -1
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDateAfterDays(days: Long): String {
    // Get the current date
    val today = LocalDate.now()

    // Calculate the date after 'days'
    val futureDate = today.plusDays(days)

    // Format the date in "dd-MM-yyyy" format
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-DD")
    return futureDate.format(formatter)
}

fun convertSecondsToTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}

fun convertToMinutes(time: String): Int {
    // Split the input into hours and minutes
    val parts = time.split(":")
    val hours = parts[0].toInt()
    val minutes = parts[1].toInt()

    // Calculate total minutes
    return hours * 60 + minutes
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateDaysBetween(startDate: String, endDate: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val start = LocalDate.parse(startDate, formatter)
        val end = LocalDate.parse(endDate, formatter)
        ChronoUnit.DAYS.between(start, end).toString()
    } catch (e: Exception) {
        "0"
    }
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}
