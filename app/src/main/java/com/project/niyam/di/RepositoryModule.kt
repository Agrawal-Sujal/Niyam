package com.project.niyam.di

import com.project.niyam.data.repository.AlarmRepositoryImpl
import com.project.niyam.data.repository.AuthRepositoryImpl
import com.project.niyam.data.repository.FlexibleTaskRepositoryImpl
import com.project.niyam.data.repository.FriendRepositoryImpl
import com.project.niyam.data.repository.SubTaskRepositoryImpl
import com.project.niyam.data.repository.SyncRepositoryImpl
import com.project.niyam.data.repository.TimeBoundTaskRepositoryImpl
import com.project.niyam.domain.repository.AlarmRepository
import com.project.niyam.domain.repository.AuthRepository
import com.project.niyam.domain.repository.FlexibleTaskRepository
import com.project.niyam.domain.repository.FriendRepository
import com.project.niyam.domain.repository.SubTaskRepository
import com.project.niyam.domain.repository.SyncRepository
import com.project.niyam.domain.repository.TimeBoundTaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTimeBoundTaskRepository(
        impl: TimeBoundTaskRepositoryImpl,
    ): TimeBoundTaskRepository

    @Binds
    @Singleton
    abstract fun bindFlexibleTaskRepository(
        impl: FlexibleTaskRepositoryImpl,
    ): FlexibleTaskRepository

    @Binds
    @Singleton
    abstract fun bindSubTaskRepository(impl: SubTaskRepositoryImpl): SubTaskRepository

    @Binds @Singleton
    abstract fun bindAlarmRepo(impl: AlarmRepositoryImpl): AlarmRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(impl: SyncRepositoryImpl): SyncRepository

    @Binds
    @Singleton
    abstract fun bindFriendRepository(impl: FriendRepositoryImpl): FriendRepository
}
