package com.project.niyam.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


data class SubTasks(
    val subTaskName: String,
    val subTaskDescription: String,
    val isCompleted: Boolean = false
)
