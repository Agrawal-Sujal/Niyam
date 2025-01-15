package com.project.niyam.domain.repository

import com.project.niyam.domain.model.Tasks
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getAllTasks(date: String): Flow<List<Tasks>>

    suspend fun insertTasks(tasks: Tasks)

    fun getTaskById(id: Int): Flow<Tasks>

    suspend fun updateTasks(tasks: Tasks)
}
