package com.project.niyam.data.repositoryImpl

import com.project.niyam.data.datasources.local.GeneralDAO
import com.project.niyam.domain.model.GeneralInfo
import com.project.niyam.domain.repository.GeneralInfoRepository
import javax.inject.Inject

class GeneralInfoRepositoryImpl @Inject constructor(private val generalDAO: GeneralDAO) :
    GeneralInfoRepository {
    override suspend fun strictTaskRunningId(): Int {
        return generalDAO.getStrictTaskRunningId()
    }

    override suspend fun normalTaskRunningId(): Int {
        return generalDAO.getNormalTaskRunningId()
    }

    override suspend fun updateGeneralInfo(generalInfo: GeneralInfo) {
        generalDAO.updateGeneralInfo(generalInfo)
    }
}
