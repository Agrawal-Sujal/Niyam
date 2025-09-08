package com.project.niyam.services.remote

import com.project.niyam.domain.models.tasks.AddedTaskResponse
import com.project.niyam.domain.models.tasks.FlexibleTaskRequest
import com.project.niyam.domain.models.tasks.SubTaskRequest
import com.project.niyam.domain.models.tasks.TimeBoundTaskRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TasksServices {

    @POST("tasks/timeboundedtasks/")
    suspend fun addTimeBoundedTask(
        @Body timeBoundTaskRequest: TimeBoundTaskRequest,
    ): Response<AddedTaskResponse>

    @POST("tasks/flexibletasks/")
    suspend fun addFlexibleTask(
        @Body flexibleTaskRequest: FlexibleTaskRequest,
    ): Response<AddedTaskResponse>

    @POST("tasks/subtasks/")
    suspend fun addSubTask(
        @Body subTaskRequest: SubTaskRequest,
    ): Response<AddedTaskResponse>
}
