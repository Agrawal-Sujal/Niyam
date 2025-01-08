package com.project.niyam.di

import android.content.Context
import androidx.room.Room
import com.project.niyam.data.datasources.local.StrictTasksDAO
import com.project.niyam.data.datasources.local.TasksDataBase
import com.project.niyam.data.repositoryImpl.StrictTaskRepositoryImpl
import com.project.niyam.domain.repository.StrictTaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TopModule {

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): TasksDataBase {
        return Room.databaseBuilder(context, TasksDataBase::class.java, "alarm_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideStrictTasksDAO(tasksDataBase: TasksDataBase): StrictTasksDAO {
        return tasksDataBase.getStrictTasksDAO()
    }

    @Provides
    fun provideStrictTaskRepoImpl(strictTaskRepositoryImpl: StrictTaskRepositoryImpl): StrictTaskRepository {
        return strictTaskRepositoryImpl
    }
}
