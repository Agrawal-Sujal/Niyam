package com.project.niyam.presentation.screens.viewmodels.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.project.niyam.domain.model.StrictTasks
import com.project.niyam.domain.model.SubTasks
import com.project.niyam.domain.repository.StrictTaskRepository
import com.project.niyam.presentation.toStrictTasks
import com.project.niyam.utils.convertToLocalTime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class CreateStrictTaskUiState(
    val name: String = "",
    val description: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val subTasks: List<CreateSubTaskUiState> = listOf(),
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

    fun updateSubTaskDescription(subTaskDescription: String) {
        _uiStateSubTask.value = _uiStateSubTask.value.copy(subTaskDescription = subTaskDescription)
    }

    fun saveSubTask() {
        val subTasks: MutableList<CreateSubTaskUiState> = _uiState.value.subTasks.toMutableList()
        subTasks.add(_uiStateSubTask.value)
        _uiState.value = _uiState.value.copy(subTasks = subTasks)
        _uiStateSubTask.value = CreateSubTaskUiState()
    }

    fun updateDate(date: String) {
        this.date = date
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateDescription(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
    }

    fun updateStartDate(startDate: String) {
        _uiState.value = _uiState.value.copy(startTime = startDate)
    }

    fun updateEndDate(endTime: String) {
        _uiState.value = _uiState.value.copy(endTime = endTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveTask(date: String) {
        repository.insertStrictTasks(_uiState.value.toStrictTasks(date = date))
        _uiState.value = CreateStrictTaskUiState()
    }
}
