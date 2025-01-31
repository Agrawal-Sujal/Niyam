package com.project.niyam.data.repositoryImpl

import com.project.niyam.data.datasources.local.GeneralDAO
import com.project.niyam.domain.model.GeneralInfo
import com.project.niyam.domain.repository.GeneralInfoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GeneralInfoRepositoryImpl @Inject constructor(private val generalDAO: GeneralDAO) :
    GeneralInfoRepository {
    override fun strictTaskRunningId(): Flow<Int> {
        return generalDAO.getStrictTaskRunningId()
    }

    override fun normalTaskRunningId(): Flow<Int> {
        return generalDAO.getNormalTaskRunningId()
    }

    override suspend fun updateGeneralInfo(generalInfo: GeneralInfo) {
        generalDAO.updateGeneralInfo(generalInfo)
    }
}
