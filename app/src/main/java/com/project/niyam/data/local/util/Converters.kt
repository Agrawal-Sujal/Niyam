package com.project.niyam.data.local.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.project.niyam.utils.TaskStatus
import java.time.LocalDate
import java.time.LocalTime

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? =
        dateString?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? = time?.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? =
        timeString?.let { LocalTime.parse(it) }

    @TypeConverter
    fun fromTaskStatus(status: TaskStatus?): String? = status?.name

    @TypeConverter
    fun toTaskStatus(status: String?): TaskStatus? =
        status?.let { TaskStatus.valueOf(it) }
}