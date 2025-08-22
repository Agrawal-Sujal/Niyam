package com.project.niyam.data.local.dao


import androidx.room.*
import com.project.niyam.data.local.entity.FlexibleTaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FlexibleTaskDao {

    @Query("SELECT * FROM flexible_task WHERE windowStartDate <= :date AND windowEndDate >= :date")
    fun getAllTask(date: LocalDate): Flow<List<FlexibleTaskEntity>>

    @Query("SELECT * FROM flexible_task WHERE id = :id")
    fun observeById(id: Long): Flow<FlexibleTaskEntity?>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: FlexibleTaskEntity): Long

    @Delete
    suspend fun deleteTask(task: FlexibleTaskEntity)

    @Update
    suspend fun updateTask(task: FlexibleTaskEntity)

    @Query("SELECT * FROM flexible_task WHERE id = :id LIMIT 1")
    suspend fun getTask(id: Int): FlexibleTaskEntity?
}