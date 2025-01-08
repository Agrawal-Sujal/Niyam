package com.project.niyam.data.datasources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.project.niyam.domain.model.StrictTasks
import kotlinx.coroutines.flow.Flow
import java.util.Date


@Dao
interface StrictTasksDAO {

    @Insert
    suspend fun insertStrictTask(strictTask: StrictTasks)

    @Query("SELECT * FROM StrictTasks WHERE  date=:date")
    fun getAllStrictAlarm(date: String): Flow<List<StrictTasks>>

}