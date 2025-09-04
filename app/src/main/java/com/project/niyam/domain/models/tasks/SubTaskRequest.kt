package com.project.niyam.domain.models.tasks

import com.google.gson.annotations.SerializedName

data class SubTaskRequest(
    @SerializedName("main_timebounded_task")
    val mainTimeBoundTask: Int?,

    @SerializedName("main_flexible_task")
    val mainFlexibleTask: Int?,

    @SerializedName("is_flexible_task")
    val isFlexibleTask: Boolean,

    @SerializedName("is_completed")
    val isCompleted: Boolean,

    @SerializedName("subtask_name")
    val subTaskName: String,

    @SerializedName("subtask_description")
    val subTaskDescription: String?
)
