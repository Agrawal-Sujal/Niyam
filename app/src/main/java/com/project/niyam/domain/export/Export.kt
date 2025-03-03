package com.project.niyam.domain.export

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.project.niyam.domain.model.StrictTasks
import com.project.niyam.domain.model.Tasks
import com.project.niyam.domain.repository.StrictTaskRepository
import com.project.niyam.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream

data class ExportData(
    val strictTask: List<StrictTasks>,
    val task: List<Tasks>
)

class Export(
    private val strictTaskRepository: StrictTaskRepository,
    private val taskRepository: TaskRepository,
    private val context: Context
) {

    val state = mutableStateOf(ExportStatus.NOT_STARTED)

    fun export(uri: Uri) {
        state.value = ExportStatus.RUNNING
        val parentFolder = DocumentFile.fromTreeUri(context, uri)
        if (parentFolder != null && parentFolder.canWrite()) {
            val newFolder = parentFolder.createDirectory("Niyam")
            if (newFolder != null) {
                val taskURI = newFolder.createFile("text/plain", "Tasks")!!.uri
                write(taskURI)
            } else state.value = ExportStatus.ERROR
        } else {
            state.value = ExportStatus.ERROR
        }
    }

    private fun write(taskURI: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            val strictTaskList: List<StrictTasks> =
                strictTaskRepository.getStrictTaskFromStarting().first()
            val taskList: List<Tasks> = taskRepository.getTasksFromStarting().first()
            val gson = Gson()
            val tasks = gson.toJson(ExportData(strictTaskList, taskList))
            delay(2000)
            try {
                taskURI.let { fileUri ->
                    context.contentResolver.openOutputStream(fileUri)
                        ?.use { outputStream: OutputStream ->
                            outputStream.write(tasks.toByteArray())
                            outputStream.flush()
                        }
                }
                state.value = ExportStatus.COMPLETED
            } catch (e: Exception) {
                state.value = ExportStatus.ERROR
                e.printStackTrace()
            }
        }
    }

    val importState = mutableStateOf(ImportStatus.NOT_STARTED)
    fun import(uri: Uri) {
        importState.value = ImportStatus.RUNNING
        val content = readContent(uri)
        if (content == null) {
            importState.value = ImportStatus.ERROR
            return
        }
        val gson = Gson()
        val type = object : TypeToken<ExportData>() {}.type

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val list: ExportData = gson.fromJson(content, type)
                val strictTask = list.strictTask
                val normalTask = list.task
                strictTask.forEach {
                    strictTaskRepository.insertStrictTasks(it.copy(id = 0))
                }
                normalTask.forEach {
                    taskRepository.insertTasks(it.copy(id = 0))
                }
                importState.value = ImportStatus.COMPLETED
            } catch (e: JsonSyntaxException) {
                importState.value = ImportStatus.ERROR
            }
        }
    }

    private fun readContent(uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

enum class ExportStatus {
    RUNNING,
    ERROR,
    COMPLETED,
    NOT_STARTED
}

enum class ImportStatus {
    NOT_STARTED, COMPLETED, ERROR, RUNNING
}