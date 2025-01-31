package com.project.niyam.domain.repository

import com.project.niyam.domain.model.GeneralInfo

interface GeneralInfoRepository {

    suspend fun strictTaskRunningId(): Int

    suspend fun normalTaskRunningId(): Int

    suspend fun updateGeneralInfo(generalInfo: GeneralInfo)
}
