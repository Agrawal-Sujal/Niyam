package com.project.niyam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.project.niyam.utils.TaskStatus
import com.project.niyam.utils.TimerState
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "flexible_task")
data class FlexibleTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val windowStartDate: LocalDate,
    val windowEndDate: LocalDate,
    val windowStartTime: LocalTime,
    val windowEndTime: LocalTime,
    val hoursAlloted: Int,
    val taskName: String,
    val taskDescription: String? = null,
    val isCompleted: Boolean = false,
    var status: TimerState = TimerState.IDLE,
    var secondsRemaining: Int,
    var remoteId: Int? = null,
    var isSynced: Boolean = false,
)
