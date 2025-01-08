package com.project.niyam.domain.repository

import com.project.niyam.domain.model.StrictTasks
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface StrictTaskRepository {

    fun getAllStrictTasks(date: String): Flow<List<StrictTasks>>

    suspend fun insertStrictTasks(strictTasks: StrictTasks)


}