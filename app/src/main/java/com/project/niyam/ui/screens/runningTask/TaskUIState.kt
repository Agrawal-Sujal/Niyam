package com.project.niyam.ui.screens.runningTask

import com.project.niyam.utils.TimerState
import java.time.LocalDate
import java.time.LocalTime

data class TaskUiState(
    val id: Long? = null,
    val taskId: Int = 0,
    val isFlexible: Boolean = false,
    val remainingTime: String = "00:00:00",
    val subTasks: List<SubTaskUi> = emptyList(),
    val currentSubTaskIndex: Int = 0,
    val timerState: TimerState = TimerState.IDLE,
    val endDate : LocalDate,
    val endTime : LocalTime
)

data class SubTaskUi(
    val id: Int,
    val name: String,
    val description: String?,
    val isCompleted: Boolean,
    val taskId : Int,
    val isFlexible: Boolean
)
