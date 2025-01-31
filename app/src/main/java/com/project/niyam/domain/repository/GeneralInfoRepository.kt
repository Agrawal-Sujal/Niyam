package com.project.niyam.domain.repository

import com.project.niyam.domain.model.GeneralInfo
import kotlinx.coroutines.flow.Flow

interface GeneralInfoRepository {

    fun strictTaskRunningId(): Flow<Int>

    fun normalTaskRunningId(): Flow<Int>

    suspend fun updateGeneralInfo(generalInfo: GeneralInfo)
}
