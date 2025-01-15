package com.project.niyam.presentation.screens.viewmodels.preview

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.domain.model.SubTasks
import com.project.niyam.domain.repository.StrictTaskRepository
import com.project.niyam.presentation.toPreviewScreenUIState
import com.project.niyam.presentation.toStrictTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PreviewScreenUIState(
    val id: Int = 0,
    val taskName: String = "",
    var taskDescription: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val isCompleted: Boolean = false,
    val subTasks: List<SubTasks> = listOf(SubTasks(), SubTasks()),
    val date: String = "",
    val currentIndex: Int = 1,
)

@HiltViewModel
class PreviewScreenViewModel @Inject constructor(private val strictTaskRepositoryImpl: StrictTaskRepository) :
    ViewModel() {

    private val _uiState = mutableStateOf(PreviewScreenUIState())
    val uiState: State<PreviewScreenUIState> = _uiState

    fun getStrictTask(id: Int) {
        viewModelScope.launch {
            try {
                strictTaskRepositoryImpl.getStrictTaskById(id)
                    .collect { strictTask ->
                        _uiState.value =
                            strictTask.toPreviewScreenUIState(_uiState.value.currentIndex)
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
        Log.i("Updated UI State", _uiState.value.toString())
        updateStrictTask()
        Log.i("uiState", _uiState.value.toString())
    }

    private suspend fun updateStrictTask() {
//        _uiState.value = _uiState.value.copy(subTasks = subTasks)
        strictTaskRepositoryImpl.updateStrictTasks(_uiState.value.toStrictTasks())
        Log.i("uiState", _uiState.value.toString())
    }
}
