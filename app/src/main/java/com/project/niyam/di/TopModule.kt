package com.project.niyam.di

import android.content.Context
import androidx.room.Room
import com.project.niyam.data.datasources.local.DatabaseCallback
import com.project.niyam.data.datasources.local.GeneralDAO
import com.project.niyam.data.datasources.local.StrictTasksDAO
import com.project.niyam.data.datasources.local.TasksDAO
import com.project.niyam.data.datasources.local.TasksDataBase
import com.project.niyam.data.repositoryImpl.GeneralInfoRepositoryImpl
import com.project.niyam.data.repositoryImpl.StrictTaskRepositoryImpl
import com.project.niyam.data.repositoryImpl.TaskRepositoryImpl
import com.project.niyam.domain.repository.GeneralInfoRepository
import com.project.niyam.domain.repository.StrictTaskRepository
import com.project.niyam.domain.repository.TaskRepository
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
            .addCallback(DatabaseCallback(context))
            .build()
    }

    @Provides
    fun provideStrictTasksDAO(tasksDataBase: TasksDataBase): StrictTasksDAO {
        return tasksDataBase.getStrictTasksDAO()
    }

    @Provides
    fun provideTasksDAO(tasksDataBase: TasksDataBase): TasksDAO {
        return tasksDataBase.getTasksDAO()
    }

    @Provides
    fun provideGeneralInfoDAO(tasksDataBase: TasksDataBase): GeneralDAO {
        return tasksDataBase.getGeneralDAO()
    }

    @Provides
    fun provideStrictTaskRepoImpl(strictTaskRepositoryImpl: StrictTaskRepositoryImpl): StrictTaskRepository {
        return strictTaskRepositoryImpl
    }

    @Provides
    fun provideTaskRepoImpl(taskRepositoryImpl: TaskRepositoryImpl): TaskRepository {
        return taskRepositoryImpl
    }

    @Provides
    fun provideGeneralInfoRepoImpl(generalInfoRepositoryImpl: GeneralInfoRepositoryImpl): GeneralInfoRepository {
        return generalInfoRepositoryImpl
    }
}
