package com.project.niyam.data.repositoryImpl

import com.project.niyam.data.datasources.local.StrictTasksDAO
import com.project.niyam.domain.model.StrictTasks
import com.project.niyam.domain.repository.StrictTaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StrictTaskRepositoryImpl @Inject constructor(private val strictTasksDAO: StrictTasksDAO) :
    StrictTaskRepository {
    override fun getAllStrictTasks(date: String): Flow<List<StrictTasks>> {
        return strictTasksDAO.getAllStrictAlarm(date)
    }

    override suspend fun insertStrictTasks(strictTasks: StrictTasks) {
        return strictTasksDAO.insertStrictTask(strictTasks)
    }

    override fun getStrictTaskById(id: Int): Flow<StrictTasks> {
        return strictTasksDAO.getStrictAlarmById(id)
    }

    override fun getEndTime(id: Int): Flow<String> {
        return strictTasksDAO.getEndTime(id)
    }

    override suspend fun updateStrictTasks(strictTasks: StrictTasks) {
        return strictTasksDAO.updateStrictTask(strictTasks)
    }

    override suspend fun deleteStrictTask(strictTask: StrictTasks) {
        return strictTasksDAO.deleteStrictTask(strictTask)
    }
}
