package com.project.niyam.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.project.niyam.data.local.dao.FlexibleTaskDao
import com.project.niyam.data.local.dao.SubTaskDao
import com.project.niyam.data.local.dao.TimeBoundTaskDao
import com.project.niyam.domain.models.tasks.TaskMapper.toRequest
import com.project.niyam.domain.repository.SyncRepository
import com.project.niyam.services.remote.TasksServices
import com.project.niyam.utils.Utils.error
import com.project.niyam.utils.Utils.parseResponse
import javax.inject.Inject

class SyncRepositoryImpl@Inject constructor(
    val timeBoundTaskDao: TimeBoundTaskDao,
    val tasksServices: TasksServices,
    val flexibleTaskDao: FlexibleTaskDao,
    val subTaskDao: SubTaskDao,
) : SyncRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun syncTimeBoundedTasks(userId: Int) {
        val tasks = timeBoundTaskDao.getAllUnSyncedTasks()
        for (task in tasks) {
            val response = tasksServices.addTimeBoundedTask(task.toRequest(userId))
            val parseResponse = parseResponse(response)
            if (response.isSuccessful) {
                task.isSynced = true
                val remoteId = parseResponse.data!!.id
                task.remoteId = remoteId
                timeBoundTaskDao.updateTask(task)
                val subTasks = subTaskDao.getSubTasks(task.id)
                for (subTask in subTasks) {
                    subTask.remoteTaskId = remoteId
                    subTaskDao.updateSubTask(subTask)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun syncFlexibleTasks(userId: Int) {
        val tasks = flexibleTaskDao.getAllUnSyncedTasks()
        for (task in tasks) {
            val response = tasksServices.addFlexibleTask(task.toRequest(userId))
            val parseResponse = parseResponse(response)
            if (response.isSuccessful) {
                task.isSynced = true
                task.remoteId = parseResponse.data!!.id
                flexibleTaskDao.updateTask(task)
                val subTasks = subTaskDao.getSubTasks(task.id)
                for (subTask in subTasks) {
                    subTask.remoteTaskId = task.remoteId
                    subTaskDao.updateSubTask(subTask)
                }
            }
        }
    }

    override suspend fun syncSubTasks(userId: Int) {
        val tasks = subTaskDao.getAllUnSyncedTasks()
        Log.d("SubTasks", tasks.toString())
        for (task in tasks) {
            val response = tasksServices.addSubTask(task.toRequest())
            val parseResponse = parseResponse(response)
            if (response.isSuccessful) {
                task.isSynced = true
                task.remoteId = parseResponse.data!!.id
                subTaskDao.updateSubTask(task)
            } else {
                val error = parseResponse.error
                Log.d("Error", error.toString())
                Log.d("Error", response.error().toString())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun syncTasks(userId: Int) {
        syncTimeBoundedTasks(userId)
        syncFlexibleTasks(userId)
        syncSubTasks(userId)
    }
}
