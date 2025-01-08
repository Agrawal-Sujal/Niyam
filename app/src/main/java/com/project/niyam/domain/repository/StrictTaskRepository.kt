package com.project.niyam.domain.repository

import com.project.niyam.domain.model.StrictTasks
import kotlinx.coroutines.flow.Flow

interface StrictTaskRepository {

    fun getAllStrictTasks(date: String): Flow<List<StrictTasks>>

    suspend fun insertStrictTasks(strictTasks: StrictTasks)
}
