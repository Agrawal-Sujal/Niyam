package com.project.niyam.domain.repository

import com.project.niyam.data.local.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun observe(id: Long, isFlexible: Boolean): Flow<AlarmEntity?>
    suspend fun get(id: Long): AlarmEntity?
    suspend fun save(alarm: AlarmEntity)
}
