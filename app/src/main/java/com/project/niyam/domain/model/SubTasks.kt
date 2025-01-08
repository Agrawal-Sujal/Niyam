package com.project.niyam.domain.model

data class SubTasks(
    val subTaskName: String,
    val subTaskDescription: String,
    val isCompleted: Boolean = false,
)
