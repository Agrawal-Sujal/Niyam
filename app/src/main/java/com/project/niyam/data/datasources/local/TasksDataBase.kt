package com.project.niyam.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.niyam.domain.model.StrictTasks

@Database(entities = [StrictTasks::class], version = 1)
abstract class TasksDataBase : RoomDatabase() {

    abstract fun getStrictTasksDAO(): StrictTasksDAO


}