package com.project.niyam.di

import com.project.niyam.data.repositoryImpl.StrictTaskRepositoryImpl
import com.project.niyam.domain.repository.StrictTaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent


//@Module
//@InstallIn(ActivityComponent::class)
//class Module {
//
//
//    @Provides
//    fun provideStrictTaskRepoImpl(strictTaskRepositoryImpl: StrictTaskRepositoryImpl): StrictTaskRepository {
//        return strictTaskRepositoryImpl
//    }
//
//
//}