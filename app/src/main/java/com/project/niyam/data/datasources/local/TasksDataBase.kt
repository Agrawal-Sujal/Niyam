package com.project.niyam.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.niyam.domain.model.GeneralInfo
import com.project.niyam.domain.model.StrictTasks
import com.project.niyam.domain.model.Tasks

@Database(entities = [StrictTasks::class, Tasks::class, GeneralInfo::class], version = 1)
abstract class TasksDataBase : RoomDatabase() {

    abstract fun getStrictTasksDAO(): StrictTasksDAO
    abstract fun getTasksDAO(): TasksDAO
    abstract fun getGeneralDAO(): GeneralDAO
}
