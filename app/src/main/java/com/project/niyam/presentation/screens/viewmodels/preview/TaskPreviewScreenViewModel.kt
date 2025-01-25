package com.project.niyam.presentation.screens.viewmodels.preview

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.domain.model.SubTasks
import com.project.niyam.domain.repository.TaskRepository
import com.project.niyam.domain.services.ServiceHelper
import com.project.niyam.presentation.toPreviewScreenUIState
import com.project.niyam.presentation.toTasks
import com.project.niyam.utils.Constants.ACTION_SERVICE_START
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PreviewScreenUIState(
    val id: Int = 0,
    val taskName: String = "",
    var taskDescription: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val isCompleted: Boolean = false,
    val subTasks: List<SubTasks> = listOf(SubTasks(), SubTasks()),
    val minutesRemaining: String = "",
    val currentIndex: Int = 0,
)

@HiltViewModel
class TaskPreviewScreenViewModel @Inject constructor(private val taskRepositoryImpl: TaskRepository) :
    ViewModel() {

    private val _uiState = mutableStateOf(PreviewScreenUIState())
    val uiState: State<PreviewScreenUIState> = _uiState
    var id: Int = 0

    fun updateID(id: Int) {
        if (id != this.id) {
            this.id = id
            getStrictTask()
        }
    }

    private fun getStrictTask() {
        viewModelScope.launch {
            try {
                taskRepositoryImpl.getTaskById(id)
                    .collect { task ->
                        _uiState.value =
                            task.toPreviewScreenUIState(_uiState.value.currentIndex)
                    }
                Log.i("uiState", _uiState.value.toString())
            } catch (e: Exception) {
                // Handle error (e.g., log it or show an error state in the UI)
            }
        }
    }

    fun increaseIndex() {
        val currentIndex = _uiState.value.currentIndex
        _uiState.value = _uiState.value.copy(currentIndex = currentIndex + 1)
        Log.i("uiState increase", _uiState.value.toString())
    }

    fun decreaseIndex() {
        val currentIndex = _uiState.value.currentIndex
        _uiState.value = _uiState.value.copy(currentIndex = currentIndex - 1)
        Log.i("uiState decrease", _uiState.value.toString())
    }

    fun subTaskDone() = viewModelScope.launch {
//        val subTask = _uiState.value.subTasks
//        subTask[_uiState.value.currentIndex].isCompleted = true
//        Log.i("uiState", _uiState.value.toString())
//        _uiState.value = _uiState.value.copy(
//            subTasks = subTask,
//        )
//        Log.i("uiState", _uiState.value.toString())
        val currentSubTasks = _uiState.value.subTasks.toMutableList()
        val currentIndex = _uiState.value.currentIndex

        // Update the specific subtask
        val updatedSubTask = currentSubTasks[currentIndex].copy(isCompleted = true)
        currentSubTasks[currentIndex] = updatedSubTask

        // Update the state with a new list
        _uiState.value = _uiState.value.copy(subTasks = currentSubTasks)

        // Log the updated UI state for debugging
//        Log.i("Updated UI State", _uiState.value.toString())
        updateStrictTask()
//        Log.i("uiState", _uiState.value.toString())
    }

    private suspend fun updateStrictTask() {
        taskRepositoryImpl.updateTasks(_uiState.value.toTasks())
//        Log.i("uiState", _uiState.value.toString())
    }
}
