package com.project.niyam.di

import android.content.Context
import androidx.room.Room
import com.project.niyam.data.local.AppDatabase
import com.project.niyam.data.local.dao.AlarmDao
import com.project.niyam.data.local.dao.FlexibleTaskDao
import com.project.niyam.data.local.dao.SubTaskDao
import com.project.niyam.data.local.dao.TimeBoundTaskDao
import com.project.niyam.data.repository.FlexibleTaskRepositoryImpl
import com.project.niyam.data.repository.TimeBoundTaskRepositoryImpl
import com.project.niyam.domain.repository.FlexibleTaskRepository
import com.project.niyam.domain.repository.TimeBoundTaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "task_manager_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTimeBoundTaskDao(db: AppDatabase): TimeBoundTaskDao = db.timeBoundTaskDao()

    @Provides
    fun provideFlexibleTaskDao(db: AppDatabase): FlexibleTaskDao = db.flexibleTaskDao()

    @Provides
    fun provideSubTaskDao(db: AppDatabase): SubTaskDao = db.subTaskDao()

    @Provides fun provideAlarmDao(db: AppDatabase): AlarmDao = db.alarmDao()

}