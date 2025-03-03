package com.project.niyam.domain.import

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.project.niyam.data.datasources.local.TasksDAO
import com.project.niyam.domain.model.StrictTasks
import com.project.niyam.domain.model.Tasks
import com.project.niyam.domain.repository.StrictTaskRepository
import com.project.niyam.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class Import2(
    private val strictTaskRepository: StrictTaskRepository,
    private val taskRepository: TaskRepository,
    private val context: Context,
    private val tasksDAO: TasksDAO
) {
//    val state = mutableStateOf(ImportStatus.NOT_STARTED)
//    fun import(uri: Uri) {
//        state.value = ImportStatus.RUNNING
//        val content = readContent(uri)
//        if (content == null) {
//            state.value = ImportStatus.ERROR
//            return
//        }
//        val gson = Gson()
//        val type1 = object : TypeToken<List<Tasks>>() {}.type
//        val type2 = object : TypeToken<List<StrictTasks>>() {}.type
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val list: List<Tasks> = gson.fromJson(content, type1)
//                list.forEach {
//                    taskRepository.insertTasks(it)
//                }
//                state.value = ImportStatus.COMPLETED
//            } catch (e: JsonSyntaxException) {
//                try {
//                    val list: List<StrictTasks> = gson.fromJson(content, type2)
//                    list.forEach {
//                        strictTaskRepository.insertStrictTasks(it)
//                    }
//                    state.value = ImportStatus.COMPLETED
//
//                } catch (e: JsonSyntaxException) {
//                    state.value = ImportStatus.ERROR
//                }
//            }
//        }
//
//    }
//
//    private fun readContent(uri: Uri): String? {
//        return try {
//            context.contentResolver.openInputStream(uri)?.use { inputStream ->
//                BufferedReader(InputStreamReader(inputStream)).use { reader ->
//                    reader.readText()
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
}

//enum class ImportStatus {
//    NOT_STARTED, COMPLETED, ERROR, RUNNING
//}