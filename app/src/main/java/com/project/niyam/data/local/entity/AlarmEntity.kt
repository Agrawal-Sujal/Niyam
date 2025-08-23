package com.project.niyam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.project.niyam.utils.TimerState
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Int,
    val isFlexible: Boolean,
    val secondsRemaining: Int,
    val state: TimerState = TimerState.IDLE,
    val endDate: LocalDate,
    val endTime: LocalTime,
)
