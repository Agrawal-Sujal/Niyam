package com.project.niyam.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.project.niyam.data.typeConverters.SubTaskConverter
import java.sql.Date
import java.sql.Time

@Entity
@TypeConverters(SubTaskConverter::class)
data class StrictTasks(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskName: String,
    val taskDescription: String,
    val startTime: String,
    val endTime: String,
    val isCompleted: Boolean = false,
    val subTasks: List<SubTasks>,
    val date: String
)
