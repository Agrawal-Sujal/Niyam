package com.project.niyam.domain.repository

interface SyncRepository {

    suspend fun syncTimeBoundedTasks(userId: Int)

    suspend fun syncFlexibleTasks(userId: Int)

    suspend fun syncSubTasks(userId: Int)

    suspend fun syncTasks(userId: Int)
}
