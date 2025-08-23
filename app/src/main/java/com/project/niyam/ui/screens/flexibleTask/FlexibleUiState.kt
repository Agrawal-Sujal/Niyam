package com.project.niyam.ui.screens.flexibleTask

import com.project.niyam.ui.screens.addTimeBoundTask.SubTaskUi
import java.time.LocalDate
import java.time.LocalTime

data class FlexibleUiState(
    val name: String = "",
    val description: String = "",
    val windowStartDate: LocalDate? = null,
    val windowEndDate: LocalDate? = null,
    val windowStartTime: LocalTime? = null,
    val windowEndTime: LocalTime? = null,
    val hoursAlloted: Int = 1,
    val subTasks: List<SubTaskUi> = emptyList(),
    val showCancelDialog: Boolean = false,
    val showSubTaskSheet: Boolean = false,
    val tempSubTaskTitle: String = "",
    val tempSubTaskDescription: String = "",
)
