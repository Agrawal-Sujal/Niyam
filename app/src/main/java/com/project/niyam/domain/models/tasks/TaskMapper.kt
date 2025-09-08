package com.project.niyam.domain.models.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import com.project.niyam.data.local.entity.FlexibleTaskEntity
import com.project.niyam.data.local.entity.SubTaskEntity
import com.project.niyam.data.local.entity.TimeBoundTaskEntity
import java.time.format.DateTimeFormatter

object TaskMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE // yyyy-MM-dd

    @RequiresApi(Build.VERSION_CODES.O)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss") // 24h format

    @RequiresApi(Build.VERSION_CODES.O)
    fun TimeBoundTaskEntity.toRequest(userId: Int): TimeBoundTaskRequest {
        return TimeBoundTaskRequest(
            user = userId,
            date = date.format(dateFormatter),
            startTime = startTime.format(timeFormatter),
            endTime = endTime.format(timeFormatter),
            status = if (isCompleted) "DONE" else "IN_PROGRESS", // you can map differently
            taskName = taskName,
            taskDescription = taskDescription,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun FlexibleTaskEntity.toRequest(userId: Int): FlexibleTaskRequest {
        return FlexibleTaskRequest(
            user = userId,
            windowStartDate = windowStartDate.format(dateFormatter),
            windowEndDate = windowEndDate.format(dateFormatter),
            windowStartTime = windowStartTime.format(timeFormatter),
            windowEndTime = windowEndTime.format(timeFormatter),
            hoursAlloted = hoursAlloted,
            status = if (isCompleted) "DONE" else "NOT_STARTED",
            taskName = taskName,
            taskDescription = taskDescription,
        )
    }

    fun SubTaskEntity.toRequest(): SubTaskRequest {
        val isFlexible = flexibleTaskId != null
        return SubTaskRequest(
            mainTimeBoundTask = if (!isFlexible) remoteTaskId else null,
            mainFlexibleTask = if (isFlexible) remoteTaskId else null,
            isFlexibleTask = isFlexible,
            isCompleted = isCompleted,
            subTaskName = subTaskName,
            subTaskDescription = subTaskDescription,
        )
    }
}
