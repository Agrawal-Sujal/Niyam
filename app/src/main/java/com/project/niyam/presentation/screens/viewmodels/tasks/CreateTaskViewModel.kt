package com.project.niyam.presentation.screens.viewmodels.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.domain.repository.TaskRepository
import com.project.niyam.presentation.toCreateTaskUiState
import com.project.niyam.presentation.toTasks
import com.project.niyam.utils.getDateAfterDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateTaskUiState(
    val name: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val subTasks: List<CreateSubTaskUiState> = listOf(),
    val minutesRemaining: String = "",
    val days: String = "",
    val loaded: Boolean = false,
)

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
) : ViewModel() {
    var date: String = ""
    private val _uiState = mutableStateOf(CreateTaskUiState())
    val uiState: State<CreateTaskUiState> = _uiState
    private val _uiStateSubTask = mutableStateOf(CreateSubTaskUiState())
    val uiStateSubTask: State<CreateSubTaskUiState> = _uiStateSubTask

    fun updateSubTaskName(subTaskName: String) {
        _uiStateSubTask.value = _uiStateSubTask.value.copy(subTaskName = subTaskName)
    }

    fun updateSubTaskDescription(subTaskDescription: String) {
        _uiStateSubTask.value = _uiStateSubTask.value.copy(subTaskDescription = subTaskDescription)
    }

    fun loadSubTask(idx: Int) {
        _uiStateSubTask.value = _uiState.value.subTasks[idx]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSTask(id: Int) {
        if (!_uiState.value.loaded) {
            viewModelScope.launch {
                val task = repository.getTaskById(id).first()
                _uiState.value = task.toCreateTaskUiState()
                _uiState.value = _uiState.value.copy(loaded = true)
            }
        }
    }

    fun saveSubTask() {
        val subTasks: MutableList<CreateSubTaskUiState> = _uiState.value.subTasks.toMutableList()
        subTasks.add(_uiStateSubTask.value)
        _uiState.value = _uiState.value.copy(subTasks = subTasks)
        _uiStateSubTask.value = CreateSubTaskUiState()
    }

    fun removeSubTask(subTaskUiState: CreateSubTaskUiState) {
        val subTasks: MutableList<CreateSubTaskUiState> = _uiState.value.subTasks.toMutableList()
        subTasks.remove(subTaskUiState)
        _uiState.value = _uiState.value.copy(subTasks = subTasks)
    }

    fun updateSubTask(idx: Int) {
        val subTasks: MutableList<CreateSubTaskUiState> = _uiState.value.subTasks.toMutableList()
        subTasks[idx] = CreateSubTaskUiState(
            subTaskName = _uiStateSubTask.value.subTaskName,
            subTaskDescription = _uiStateSubTask.value.subTaskDescription,
            isCompleted = _uiStateSubTask.value.isCompleted,
        )
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDays(days: String) {
        _uiState.value = _uiState.value.copy(days = days)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDay(startDate: String) {
//        val startDate = DateTimeDetail.FULL_DATE.getDetail()
        val endDate = getDateAfterDays(_uiState.value.days.toLong() - 1L, startDate = startDate)
        _uiState.value = _uiState.value.copy(startDate = startDate, endDate = endDate)
    }

    fun updateMinutes(minutes: String) {
        _uiState.value = _uiState.value.copy(minutesRemaining = minutes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveTask(startDate: String) {
        updateDay(startDate)
        repository.insertTasks(_uiState.value.toTasks())
        _uiState.value = CreateTaskUiState()
    }

    fun cancel() {
        _uiState.value = CreateTaskUiState()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateTask(id: Int, startDate: String) {
        updateDay(startDate)
        repository.updateTasks(_uiState.value.toTasks(id = id))
        _uiState.value = CreateTaskUiState()
    }
}
