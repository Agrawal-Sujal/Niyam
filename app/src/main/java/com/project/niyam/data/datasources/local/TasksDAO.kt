package com.project.niyam.data.datasources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.project.niyam.domain.model.Tasks
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDAO {
    @Insert
    suspend fun insertTask(strictTask: Tasks)

    @Query("SELECT * FROM Tasks WHERE  startDate<=:date AND  endDate>=:date")
    fun getAllTasks(date: String): Flow<List<Tasks>>

    @Query("SELECT * FROM Tasks WHERE id=:id")
    fun getTasksById(id: Int): Flow<Tasks>

    @Update
    suspend fun updateTask(task: Tasks)

    @Query("SELECT minutesRemaining FROM Tasks WHERE id=:id")
    fun getEndTime(id:Int):Flow<String>
}
