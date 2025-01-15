package com.project.niyam.data.repositoryImpl

import com.project.niyam.data.datasources.local.TasksDAO
import com.project.niyam.domain.model.Tasks
import com.project.niyam.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(private val tasksDAO: TasksDAO) : TaskRepository {
    override fun getAllTasks(date: String): Flow<List<Tasks>> {
        return tasksDAO.getAllTasks(date)
    }

    override suspend fun insertTasks(tasks: Tasks) {
        return tasksDAO.insertTask(tasks)
    }

    override fun getTaskById(id: Int): Flow<Tasks> {
        return tasksDAO.getTasksById(id)
    }

    override suspend fun updateTasks(tasks: Tasks) {
        return tasksDAO.updateTask(tasks)
    }
}