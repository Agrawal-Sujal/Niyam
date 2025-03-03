package com.project.niyam.data.datasources.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.project.niyam.domain.model.StrictTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface StrictTasksDAO {

    @Insert
    suspend fun insertStrictTask(strictTask: StrictTasks)

    @Query("SELECT * FROM StrictTasks WHERE  date=:date")
    fun getAllStrictAlarm(date: String): Flow<List<StrictTasks>>

    @Query("SELECT * FROM StrictTasks WHERE id=:id")
    fun getStrictAlarmById(id: Int): Flow<StrictTasks>

    @Query("SELECT endTime FROM StrictTasks WHERE id=:id")
    fun getEndTime(id: Int): Flow<String>

    @Update
    suspend fun updateStrictTask(strictTask: StrictTasks)

    @Delete
    suspend fun deleteStrictTask(strictTask: StrictTasks)

    @Query("SELECT * FROM StrictTasks")
     fun getStrictTaskFromStarting():Flow<List<StrictTasks>>
}
