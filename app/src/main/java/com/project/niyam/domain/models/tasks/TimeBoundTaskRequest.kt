package com.project.niyam.domain.models.tasks

import com.google.gson.annotations.SerializedName

data class TimeBoundTaskRequest(
    val user: Int,
    val date: String,

    @SerializedName("start_time")
    val startTime: String,

    @SerializedName("end_time")
    val endTime: String,

    val status: String,

    @SerializedName("task_name")
    val taskName: String,

    @SerializedName("task_description")
    val taskDescription: String?,
)
