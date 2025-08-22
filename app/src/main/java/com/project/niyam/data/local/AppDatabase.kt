package com.project.niyam.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.project.niyam.data.local.dao.AlarmDao
import com.project.niyam.data.local.dao.FlexibleTaskDao
import com.project.niyam.data.local.dao.SubTaskDao
import com.project.niyam.data.local.dao.TimeBoundTaskDao
import com.project.niyam.data.local.entity.AlarmEntity
import com.project.niyam.data.local.entity.FlexibleTaskEntity
import com.project.niyam.data.local.entity.SubTaskEntity
import com.project.niyam.data.local.entity.TimeBoundTaskEntity
import com.project.niyam.data.local.util.Converters

@Database(
    entities = [
        TimeBoundTaskEntity::class,
        FlexibleTaskEntity::class,
        SubTaskEntity::class,
        AlarmEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun timeBoundTaskDao(): TimeBoundTaskDao
    abstract fun flexibleTaskDao(): FlexibleTaskDao
    abstract fun subTaskDao(): SubTaskDao
    abstract fun alarmDao(): AlarmDao
}