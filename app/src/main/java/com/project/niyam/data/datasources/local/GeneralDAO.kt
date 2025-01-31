package com.project.niyam.data.datasources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.project.niyam.domain.model.GeneralInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneralDAO {
    @Update
    suspend fun updateGeneralInfo(generalInfo: GeneralInfo)

    @Query("SELECT strictTaskRunningId FROM generalinfo WHERE id=0")
    fun getStrictTaskRunningId(): Flow<Int>

    @Query("SELECT normalTaskRunningId FROM generalinfo WHERE id=0")
    fun getNormalTaskRunningId(): Flow<Int>

    @Insert
    suspend fun insertGeneralInfo(generalInfo: GeneralInfo)

    @Query("SELECT COUNT(*) FROM generalinfo")
    suspend fun getCount(): Int
}
