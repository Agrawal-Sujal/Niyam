package com.project.niyam.data.repository

import com.project.niyam.data.local.dao.AlarmDao
import com.project.niyam.data.local.entity.AlarmEntity
import com.project.niyam.domain.repository.AlarmRepository
import com.project.niyam.domain.repository.FlexibleTaskRepository
import com.project.niyam.domain.repository.TimeBoundTaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepositoryImpl @Inject constructor(
    private val dao: AlarmDao,
    private val timeBoundTaskRepository: TimeBoundTaskRepository,
    private val flexibleTaskRepository: FlexibleTaskRepository,
) : AlarmRepository {
    override fun observe(id: Long, isFlexible: Boolean): Flow<AlarmEntity?> =
        dao.observeById(id, isFlexible)

    override suspend fun get(id: Long): AlarmEntity? = dao.getByIdOnce(id)
    override suspend fun save(alarm: AlarmEntity) {
        if (!alarm.isFlexible) {
            val task = timeBoundTaskRepository.getTask(id = alarm.taskId)
            task!!.status = alarm.state
            task.secondsRemaining = alarm.secondsRemaining
            timeBoundTaskRepository.updateTask(task)
        } else {
            val task = flexibleTaskRepository.getTask(id = alarm.taskId)
            task!!.status = alarm.state
            task.secondsRemaining = alarm.secondsRemaining
            flexibleTaskRepository.updateTask(task)
        }
        dao.upsert(alarm)
    }

    override suspend fun getByTaskId(
        taskId: Long,
        isFlexible: Boolean,
    ): AlarmEntity? = dao.getByTaskId(taskId, isFlexible)
}
