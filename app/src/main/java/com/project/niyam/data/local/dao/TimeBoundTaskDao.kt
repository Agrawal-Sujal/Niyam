package com.project.niyam.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.project.niyam.data.local.entity.TimeBoundTaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TimeBoundTaskDao {

    @Query("SELECT * FROM time_bound_task WHERE date = :date")
    fun getAllTask(date: LocalDate): Flow<List<TimeBoundTaskEntity>>

    @Query("SELECT * FROM time_bound_task WHERE isSynced = 0")
    fun getAllUnSyncedTasks(): List<TimeBoundTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TimeBoundTaskEntity): Long

    @Delete
    suspend fun deleteTask(task: TimeBoundTaskEntity)

    @Update
    suspend fun updateTask(task: TimeBoundTaskEntity)

    @Query("SELECT * FROM time_bound_task WHERE id = :id LIMIT 1")
    suspend fun getTask(id: Int): TimeBoundTaskEntity?

    @Query("SELECT * FROM time_bound_task WHERE id = :id LIMIT 1")
    fun observeTask(id: Int): Flow<TimeBoundTaskEntity?>
}
