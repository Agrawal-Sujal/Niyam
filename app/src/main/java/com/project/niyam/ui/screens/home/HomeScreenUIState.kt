package com.project.niyam.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import com.project.niyam.utils.TaskStatus
import java.time.LocalDate
import java.time.LocalTime

data class TimeBoundTaskUI(
    val id: Int,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val taskName: String,
    val taskDescription: String?,
    val completed: Boolean
)

data class FlexibleTaskUI(
    val id: Int,
    val windowStartDate: LocalDate,
    val windowEndDate: LocalDate,
    val windowStartTime: LocalTime,
    val windowEndTime: LocalTime,
    val daysRemaining: Long,
    val isFirstDay: Boolean,
    val isLastDay: Boolean,
    val hoursAlloted: Int,
    val taskName: String,
    val taskDescription: String?,
    val completed: Boolean
)

data class HomeScreenUIState @RequiresApi(Build.VERSION_CODES.O) constructor(
    val selectedDate: LocalDate = LocalDate.now(),
    val weekDates: List<LocalDate> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val timeBoundTasks: List<TimeBoundTaskUI> = emptyList(),
    val flexibleTasks: List<FlexibleTaskUI> = emptyList()
)