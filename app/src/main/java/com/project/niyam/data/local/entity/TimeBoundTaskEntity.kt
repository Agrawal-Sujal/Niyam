package com.project.niyam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.project.niyam.utils.TaskStatus
import com.project.niyam.utils.TimerState
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
    val isCompleted: Boolean = false,
    var status: TimerState = TimerState.IDLE,
    var secondsRemaining: Int,
    var remoteId: Int? = null,
    var isSynced: Boolean = false,
)
