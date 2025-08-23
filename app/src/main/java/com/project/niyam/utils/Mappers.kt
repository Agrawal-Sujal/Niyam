package com.project.niyam.utils

import android.R.attr.description
import android.R.attr.name
import android.os.Build
import androidx.annotation.RequiresApi
import com.project.niyam.data.local.entity.AlarmEntity
import com.project.niyam.data.local.entity.FlexibleTaskEntity
import com.project.niyam.data.local.entity.SubTaskEntity
import com.project.niyam.data.local.entity.TimeBoundTaskEntity
import com.project.niyam.ui.screens.addTimeBoundTask.SubTaskUi
import com.project.niyam.ui.screens.addTimeBoundTask.UiState
import com.project.niyam.ui.screens.flexibleTask.FlexibleUiState
import com.project.niyam.ui.screens.home.FlexibleTaskUI
import com.project.niyam.ui.screens.home.TimeBoundTaskUI
import com.project.niyam.ui.screens.runningTask.TaskUiState
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun FlexibleUiState.toEntity(): FlexibleTaskEntity {
    return FlexibleTaskEntity(
        windowStartDate = windowStartDate!!,
        windowEndDate = windowEndDate!!,
        windowStartTime = windowStartTime!!,
        windowEndTime = windowEndTime!!,
        hoursAlloted = hoursAlloted,
        taskName = name,
        taskDescription = description,
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun UiState.toEntity(): TimeBoundTaskEntity {
    val date: LocalDate = LocalDate.now()
    return TimeBoundTaskEntity(
        startTime = startTime!!,
        endTime = endTime!!,
        taskName = name,
        taskDescription = description,
        date = date,
    )
}

fun FlexibleUiState.toAlarmEntity(id: Int): AlarmEntity {
    return AlarmEntity(
        taskId = id,
        isFlexible = true,
        secondsRemaining = hoursAlloted * 60,
        endDate = windowEndDate!!,
        endTime = windowEndTime!!,
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun UiState.toAlarmEntity(id: Int): AlarmEntity {
    return AlarmEntity(
        taskId = id,
        isFlexible = false,
        secondsRemaining = 60,
        endTime = endTime!!,
        endDate = LocalDate.now(),
    )
}

fun SubTaskUi.toEntity(taskId: Int, isFlexible: Boolean): SubTaskEntity {
    return SubTaskEntity(
        flexibleTaskId = if (isFlexible) taskId else null,
        timeBoundTaskId = if (!isFlexible) taskId else null,
        subTaskName = title,
        subTaskDescription = description,
    )
}

fun TimeBoundTaskEntity.toUI(): TimeBoundTaskUI =
    TimeBoundTaskUI(
        id = id,
        date = date,
        startTime = startTime,
        endTime = endTime,
        taskName = taskName,
        taskDescription = taskDescription,
        completed = completed,
    )

@RequiresApi(Build.VERSION_CODES.O)
fun FlexibleTaskEntity.toUI(day: LocalDate): FlexibleTaskUI {
    val daysRemaining = ChronoUnit.DAYS.between(day, windowEndDate).coerceAtLeast(0)
    val first = day.isEqual(windowStartDate)
    val last = day.isEqual(windowEndDate)
    return FlexibleTaskUI(
        id = id,
        windowStartDate = windowStartDate,
        windowEndDate = windowEndDate,
        windowStartTime = windowStartTime,
        windowEndTime = windowEndTime,
        daysRemaining = daysRemaining,
        isFirstDay = first,
        isLastDay = last,
        hoursAlloted = hoursAlloted,
        taskName = taskName,
        taskDescription = taskDescription,
        completed = completed,
    )
}

fun AlarmEntity.toUiState(subTasks: List<SubTaskEntity>): TaskUiState {
    return TaskUiState(
        taskId = taskId,
        isFlexible = isFlexible,
        remainingTime = secondsRemaining.toHms(),
        subTasks = subTasks.map { it.toUi() },
        timerState = state,
        id = id,
        endTime = endTime,
        endDate = endDate,
    )
}

fun SubTaskEntity.toUi(): com.project.niyam.ui.screens.runningTask.SubTaskUi {
    val isFlexible = flexibleTaskId != null
    val taskId = if (isFlexible) flexibleTaskId else timeBoundTaskId!!
    return com.project.niyam.ui.screens.runningTask.SubTaskUi(
        id = id,
        name = subTaskName,
        description = subTaskDescription,
        isCompleted = isCompleted,
        taskId = taskId,
        isFlexible = isFlexible,
    )
}
fun com.project.niyam.ui.screens.runningTask.SubTaskUi.toEntity(): SubTaskEntity {
    return SubTaskEntity(
        flexibleTaskId = if (isFlexible) taskId else null,
        timeBoundTaskId = if (!isFlexible) taskId else null,
        subTaskName = name,
        subTaskDescription = description,
        isCompleted = isCompleted,

    )
}

fun TaskUiState.toAlarmEntity(): AlarmEntity {
    return AlarmEntity(
        taskId = taskId,
        isFlexible = isFlexible,
        secondsRemaining = remainingTime.toSeconds(),
        state = timerState,
        endDate = endDate,
        endTime = endTime,
    )
}
