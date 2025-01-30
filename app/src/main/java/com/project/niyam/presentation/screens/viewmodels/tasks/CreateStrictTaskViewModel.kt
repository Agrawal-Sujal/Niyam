package com.project.niyam.presentation.screens.viewmodels.tasks

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.domain.repository.StrictTaskRepository
import com.project.niyam.presentation.toCreateStrictTaskUiState
import com.project.niyam.presentation.toStrictTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateStrictTaskUiState(
    val name: String = "",
    val description: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val subTasks: List<CreateSubTaskUiState> = listOf(),
    val startH: String = "",
    val endH: String = "",
    val startMin: String = "",
    val endMIn: String = "",
    val loaded: Boolean = false
)

data class CreateSubTaskUiState(
    val subTaskName: String = "",
    val subTaskDescription: String = "",
)

@HiltViewModel
class CreateStrictTaskViewModel @Inject constructor(
    private val repository: StrictTaskRepository,
) :
    ViewModel() {
    var date: String = ""
    private val _uiState = mutableStateOf(CreateStrictTaskUiState())
    val uiState: State<CreateStrictTaskUiState> = _uiState
    private val _uiStateSubTask = mutableStateOf(CreateSubTaskUiState())
    val uiStateSubTask: State<CreateSubTaskUiState> = _uiStateSubTask

    fun updateSubTaskName(subTaskName: String) {
        _uiStateSubTask.value = _uiStateSubTask.value.copy(subTaskName = subTaskName)
    }

    fun loadStrictTask(id: Int) {
        if (!_uiState.value.loaded) {
            viewModelScope.launch {
                val task = repository.getStrictTaskById(id).first()
                _uiState.value = task.toCreateStrictTaskUiState()
                _uiState.value = _uiState.value.copy(loaded = true)
            }
        }
    }

    fun loadStrictSubTask(idx: Int) {
        _uiStateSubTask.value = _uiState.value.subTasks[idx]
    }

    fun updateSubTaskDescription(subTaskDescription: String) {
        _uiStateSubTask.value = _uiStateSubTask.value.copy(subTaskDescription = subTaskDescription)
    }

    fun saveSubTask() {
        val subTasks: MutableList<CreateSubTaskUiState> = _uiState.value.subTasks.toMutableList()
        subTasks.add(_uiStateSubTask.value)
        _uiState.value = _uiState.value.copy(subTasks = subTasks)
        _uiStateSubTask.value = CreateSubTaskUiState()
    }

    fun removeSubTask(subTaskUIState: CreateSubTaskUiState) {
        val subTasks: MutableList<CreateSubTaskUiState> = _uiState.value.subTasks.toMutableList()
        subTasks.remove(subTaskUIState)
        _uiState.value = _uiState.value.copy(subTasks = subTasks)
    }

    fun updateSubTask(idx: Int) {
        val subTasks: MutableList<CreateSubTaskUiState> = _uiState.value.subTasks.toMutableList()
        subTasks[idx] = CreateSubTaskUiState(
            subTaskName = _uiStateSubTask.value.subTaskName,
            subTaskDescription = _uiStateSubTask.value.subTaskDescription
        )
        _uiState.value = _uiState.value.copy(subTasks = subTasks)
        _uiStateSubTask.value = CreateSubTaskUiState()
    }

//    suspend fun updateSubTaskWithId(idx: Int) {
//        val subTasks: MutableList<CreateSubTaskUiState> = _uiState.value.subTasks.toMutableList()
//        subTasks[idx] = CreateSubTaskUiState(
//            subTaskName = _uiStateSubTask.value.subTaskName,
//            subTaskDescription = _uiStateSubTask.value.subTaskDescription
//        )
//        _uiState.value = _uiState.value.copy(subTasks = subTasks)
//        _uiStateSubTask.value = CreateSubTaskUiState()
//        Log.d(
//            "Testing",
//            "Update Sub Task With Id is called " + _uiState.value.subTasks[idx].toString()
//        )
//        repository.updateStrictTasks(_uiState.value.toStrictTasks(date = date))
//    }

//    fun saveSubTaskWithId() {
//        val subTasks: MutableList<CreateSubTaskUiState> = _uiState.value.subTasks.toMutableList()
//        subTasks.add(_uiStateSubTask.value)
//        _uiState.value = _uiState.value.copy(subTasks = subTasks)
//        _uiStateSubTask.value = CreateSubTaskUiState()
//        viewModelScope.launch {
//            repository.updateStrictTasks(_uiState.value.toStrictTasks(date = date))
//        }
//    }

    fun updateDate(date: String) {
        this.date = date
    }

    fun updateStartH(hour: String) {
        _uiState.value = _uiState.value.copy(startH = hour)
    }

    fun updateStartMin(min: String) {
        _uiState.value = _uiState.value.copy(startMin = min)
    }

    fun updateEndH(hour: String) {
        _uiState.value = _uiState.value.copy(endH = hour)
    }

    fun updateEndMin(min: String) {
        _uiState.value = _uiState.value.copy(endMIn = min)
    }


    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateDescription(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
    }

    private fun updateStartDate(startDate: String) {
        _uiState.value = _uiState.value.copy(startTime = startDate)
    }

    private fun updateEndDate(endTime: String) {
        _uiState.value = _uiState.value.copy(endTime = endTime)
    }

    fun checkTask(): Boolean {
        val info = _uiState.value
        return !(info.name == "" || info.endH == "" || info.endMIn == "" || info.startMin == "" || info.startH == "" || info.subTasks.isEmpty())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveTask(date: String) {
        var startHour: String = _uiState.value.startH
        if (startHour.length == 1)
            startHour = "0$startHour"
        var startMin: String = _uiState.value.startMin
        if (startMin.length == 1)
            startMin = "0$startMin"
        var endH: String = _uiState.value.endH
        if (endH.length == 1)
            endH = "0$endH"
        var endMin: String = _uiState.value.endMIn
        if (endMin.length == 1)
            endMin = "0$endMin"

        val start: String = "$startHour:$startMin"
        val end: String = "$endH:$endMin"
        updateStartDate(start)
        updateEndDate(end)
        repository.insertStrictTasks(_uiState.value.toStrictTasks(date = date))
        _uiState.value = CreateStrictTaskUiState()
    }

    suspend fun updateTask(date: String,id:String) {

        Log.d("Testing", "_uiState.value : " + _uiState.value.toString())
        var startHour: String = _uiState.value.startH
        if (startHour.length == 1)
            startHour = "0$startHour"
        var startMin: String = _uiState.value.startMin
        if (startMin.length == 1)
            startMin = "0$startMin"
        var endH: String = _uiState.value.endH
        if (endH.length == 1)
            endH = "0$endH"
        var endMin: String = _uiState.value.endMIn
        if (endMin.length == 1)
            endMin = "0$endMin"

        val start: String = "$startHour:$startMin"
        val end: String = "$endH:$endMin"
        updateStartDate(start)
        updateEndDate(end)
        repository.updateStrictTasks(_uiState.value.toStrictTasks(date = date,id))
        _uiState.value = CreateStrictTaskUiState()
    }
}
