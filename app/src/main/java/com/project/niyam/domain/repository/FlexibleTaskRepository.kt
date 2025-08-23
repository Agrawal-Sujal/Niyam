package com.project.niyam.domain.repository

import com.project.niyam.data.local.entity.FlexibleTaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface FlexibleTaskRepository {
    fun getAllTask(date: LocalDate): Flow<List<FlexibleTaskEntity>>
    suspend fun insertTask(task: FlexibleTaskEntity): Long
    suspend fun deleteTask(id: Int)
    suspend fun updateTask(task: FlexibleTaskEntity)
    suspend fun getTask(id: Int): FlexibleTaskEntity?

    fun observe(id: Long): Flow<FlexibleTaskEntity?>
}
