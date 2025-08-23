package com.project.niyam.ui.screens.addTimeBoundTask

import java.time.LocalTime

data class UiState(
    val name: String = "",
    val description: String = "",
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val subTasks: List<SubTaskUi> = emptyList(),
    val showCancelDialog: Boolean = false,
    val showSubTaskSheet: Boolean = false,
    val tempSubTaskTitle: String = "",
    val tempSubTaskDescription: String = "",
)
data class SubTaskUi(val title: String, val description: String)
