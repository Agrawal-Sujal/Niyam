package com.project.niyam.domain.models.tasks

import com.google.gson.annotations.SerializedName

data class FlexibleTaskRequest(
    val user: Int,

    @SerializedName("window_start_date")
    val windowStartDate: String,

    @SerializedName("window_end_date")
    val windowEndDate: String,

    @SerializedName("window_start_time")
    val windowStartTime: String,

    @SerializedName("window_end_time")
    val windowEndTime: String,

    @SerializedName("hours_alloted")
    val hoursAlloted: Int,

    val status: String,

    @SerializedName("task_name")
    val taskName: String,

    @SerializedName("task_description")
    val taskDescription: String?,
)
