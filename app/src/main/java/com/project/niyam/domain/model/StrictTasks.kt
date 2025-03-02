package com.project.niyam.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.project.niyam.data.typeConverters.SubTaskConverter

@Entity
@TypeConverters(SubTaskConverter::class)
data class StrictTasks(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskName: String = "",
    var taskDescription: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val isCompleted: Int = 0,
    val subTasks: List<SubTasks> = listOf(),
    val date: String = "",
)
