package com.project.niyam.domain.model

data class SubTasks(
    val subTaskName: String = "",
    val subTaskDescription: String = "",
    var isCompleted: Boolean = false,
)
