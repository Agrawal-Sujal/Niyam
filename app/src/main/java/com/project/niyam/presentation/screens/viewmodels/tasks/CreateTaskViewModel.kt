package com.project.niyam.presentation.screens.viewmodels.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.project.niyam.domain.repository.StrictTaskRepository
import com.project.niyam.domain.repository.TaskRepository
import com.project.niyam.presentation.toTasks
import com.project.niyam.utils.DateTimeDetail
import com.project.niyam.utils.getDateAfterDays
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


data class CreateTaskUiState(
    val name: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val subTasks: List<CreateSubTaskUiState> = listOf(),
    val minutesRemaining: String = "",
    val days: String = ""
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDays(days: String) {
        val startDate = DateTimeDetail.FULL_DATE.getDetail()
        val endDate = getDateAfterDays(days.toString().toLong()-1L)
        _uiState.value = _uiState.value.copy(startDate = startDate, endDate = endDate, days = days)
    }

    fun updateMinutes(minutes: String) {
        _uiState.value = _uiState.value.copy(minutesRemaining = minutes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveTask(date: String) {
        repository.insertTasks(_uiState.value.toTasks(date = date,))
        _uiState.value = CreateTaskUiState()
    }
}
