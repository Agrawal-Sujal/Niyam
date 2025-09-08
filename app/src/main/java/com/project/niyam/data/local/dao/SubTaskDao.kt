package com.project.niyam.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.project.niyam.data.local.entity.SubTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao {

    @Query("SELECT * FROM sub_task WHERE (timeBoundTaskId = :mainTaskId AND :isFlexible=0) OR (flexibleTaskId = :mainTaskId AND :isFlexible=1)")
    fun getAllSubTask(mainTaskId: Int, isFlexible: Boolean): Flow<List<SubTaskEntity>>

    @Query("SELECT * FROM sub_task WHERE isSynced = 0  AND remoteTaskId IS NOT NULL")
    fun getAllUnSyncedTasks(): List<SubTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: SubTaskEntity): Long

    @Delete
    suspend fun deleteSubTask(subTask: SubTaskEntity)

    @Update
    suspend fun updateSubTask(subTask: SubTaskEntity)

    @Query("SELECT * FROM sub_task WHERE id = :id LIMIT 1")
    suspend fun getSubTask(id: Int): SubTaskEntity?

    @Query("SELECT * FROM sub_task WHERE timeBoundTaskId = :mainTaskId OR flexibleTaskId = :mainTaskId")
    suspend fun getSubTasks(mainTaskId: Int): List<SubTaskEntity>
}
