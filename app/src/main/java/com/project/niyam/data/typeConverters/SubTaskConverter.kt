package com.project.niyam.data.typeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.niyam.domain.model.SubTasks

class SubTaskConverter {
    @TypeConverter
    fun fromSubTasksList(subTasks: List<SubTasks>): String {
        val gson = Gson()
        return gson.toJson(subTasks)
    }

    @TypeConverter
    fun toSubTasksList(subTasksString: String): List<SubTasks> {
        val gson = Gson()
        val type = object : TypeToken<List<SubTasks>>() {}.type
        return gson.fromJson(subTasksString, type)
    }
}
