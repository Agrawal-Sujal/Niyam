package com.project.niyam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "time_bound_task")
data class TimeBoundTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val taskName: String,
    val taskDescription: String? = null,
    val completed: Boolean = false,
    val remoteId: Int? = null,
    val isSynced: Boolean = false,
)
