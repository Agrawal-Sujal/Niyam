package com.project.niyam.data.repository

import com.project.niyam.data.local.dao.FlexibleTaskDao
import com.project.niyam.data.local.entity.FlexibleTaskEntity
import com.project.niyam.domain.repository.FlexibleTaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlexibleTaskRepositoryImpl @Inject constructor(
    private val dao: FlexibleTaskDao,
) : FlexibleTaskRepository {

    override fun getAllTask(date: LocalDate): Flow<List<FlexibleTaskEntity>> =
        dao.getAllTask(date)

    override suspend fun insertTask(task: FlexibleTaskEntity): Long =
        dao.insertTask(task)

    override suspend fun deleteTask(id: Int) {
        val task = dao.getTask(id)
        task?.let { dao.deleteTask(it) }
    }

    override suspend fun updateTask(task: FlexibleTaskEntity) =
        dao.updateTask(task)

    override suspend fun getTask(id: Int): FlexibleTaskEntity? =
        dao.getTask(id)

    override fun observe(id: Long): Flow<FlexibleTaskEntity?> = dao.observeById(id)
}
