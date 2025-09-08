package com.project.niyam.data.repository

import com.project.niyam.data.local.dao.TimeBoundTaskDao
import com.project.niyam.data.local.entity.TimeBoundTaskEntity
import com.project.niyam.domain.repository.TimeBoundTaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeBoundTaskRepositoryImpl @Inject constructor(
    private val dao: TimeBoundTaskDao,
) : TimeBoundTaskRepository {

    override fun getAllTask(date: LocalDate): Flow<List<TimeBoundTaskEntity>> =
        dao.getAllTask(date)

    override suspend fun insertTask(task: TimeBoundTaskEntity): Long =
        dao.insertTask(task)

    override suspend fun deleteTask(id: Int) {
        val task = dao.getTask(id)
        task?.let { dao.deleteTask(it) }
    }

    override suspend fun updateTask(task: TimeBoundTaskEntity) =
        dao.updateTask(task)

    override suspend fun getTask(id: Int): TimeBoundTaskEntity? =
        dao.getTask(id)

    override suspend fun observeTask(id: Int): Flow<TimeBoundTaskEntity?> = dao.observeTask(id)
}
