package com.project.niyam.data.datasources.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.project.niyam.domain.model.GeneralInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            val database = Room.databaseBuilder(
                context,
                TasksDataBase::class.java,
                "alarm_db",
            ).build()
            val generalDAO = database.getGeneralDAO()

            // Ensure the database is empty before inserting default data
            if (generalDAO.getCount() == 0) {
                generalDAO.insertGeneralInfo(
                    GeneralInfo(
                        strictTaskRunningId = 0,
                        normalTaskRunningId = 0,
                    ),
                )
            }
        }
    }
}
