package com.project.niyam.data.repository

import com.project.niyam.data.local.dao.AlarmDao
import com.project.niyam.data.local.entity.AlarmEntity
import com.project.niyam.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepositoryImpl @Inject constructor(
    private val dao: AlarmDao
) : AlarmRepository {
    override fun observe(id: Long,isFlexible: Boolean): Flow<AlarmEntity?> = dao.observeById(id,isFlexible)
    override suspend fun get(id: Long): AlarmEntity? = dao.getByIdOnce(id)
    override suspend fun save(alarm: AlarmEntity) = dao.upsert(alarm)
}