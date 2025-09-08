package com.project.niyam.domain.repository

import com.project.niyam.data.local.entity.SubTaskEntity
import kotlinx.coroutines.flow.Flow

interface SubTaskRepository {
    fun getAllSubTask(mainTaskId: Int, isFlexible: Boolean): Flow<List<SubTaskEntity>>
    suspend fun insertSubTask(subTask: SubTaskEntity): Long
    suspend fun deleteSubTask(id: Int)
    suspend fun updateSubTask(subTask: SubTaskEntity)
    suspend fun getSubTask(id: Int): SubTaskEntity?
}
