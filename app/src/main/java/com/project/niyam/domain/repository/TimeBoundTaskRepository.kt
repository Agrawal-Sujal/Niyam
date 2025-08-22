package com.project.niyam.domain.repository

import com.project.niyam.data.local.entity.TimeBoundTaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TimeBoundTaskRepository {
    fun getAllTask(date: LocalDate): Flow<List<TimeBoundTaskEntity>>
    suspend fun insertTask(task: TimeBoundTaskEntity): Long
    suspend fun deleteTask(id: Int)
    suspend fun updateTask(task: TimeBoundTaskEntity)
    suspend fun getTask(id: Int): TimeBoundTaskEntity?
}