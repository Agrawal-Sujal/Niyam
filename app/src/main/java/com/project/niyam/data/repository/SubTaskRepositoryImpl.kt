package com.project.niyam.data.repository

import com.project.niyam.data.local.dao.SubTaskDao
import com.project.niyam.data.local.entity.SubTaskEntity
import com.project.niyam.domain.repository.SubTaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubTaskRepositoryImpl @Inject constructor(
    private val dao: SubTaskDao,
) : SubTaskRepository {

    override fun getAllSubTask(mainTaskId: Int): Flow<List<SubTaskEntity>> =
        dao.getAllSubTask(mainTaskId)

    override suspend fun insertSubTask(subTask: SubTaskEntity): Long =
        dao.insertSubTask(subTask)

    override suspend fun deleteSubTask(id: Int) {
        val task = dao.getSubTask(id)
        task?.let { dao.deleteSubTask(it) }
    }

    override suspend fun updateSubTask(subTask: SubTaskEntity) =
        dao.updateSubTask(subTask)

    override suspend fun getSubTask(id: Int): SubTaskEntity? =
        dao.getSubTask(id)
}
