package com.project.niyam.presentation

import android.util.Log
import com.project.niyam.domain.model.StrictTasks
import com.project.niyam.presentation.screens.viewmodels.preview.PreviewScreenUIState

fun StrictTasks.toPreviewScreenUIState(currentIndex: Int): PreviewScreenUIState {
    Log.i("uiState", "Preview mapper called")
    return PreviewScreenUIState(
        id = this.id,
        taskName = this.taskName,
        taskDescription = this.taskDescription,
        startTime = this.startTime,
        endTime = this.endTime,
        isCompleted = this.isCompleted,
        subTasks = this.subTasks,
        date = this.date,
        currentIndex = currentIndex
    )
}

fun PreviewScreenUIState.toStrictTasks(): StrictTasks {
    Log.i("uiState", "Preview mapper of task called")
    return StrictTasks(
        id = this.id,
        taskName = this.taskName,
        taskDescription = this.taskDescription,
        startTime = this.startTime,
        endTime = this.endTime,
        isCompleted = this.isCompleted,
        subTasks = this.subTasks,
        date = this.date
    )
}