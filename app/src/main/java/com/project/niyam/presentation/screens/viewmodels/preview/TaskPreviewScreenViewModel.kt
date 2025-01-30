package com.project.niyam.presentation.screens.viewmodels.preview

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.domain.model.SubTasks
import com.project.niyam.domain.repository.TaskRepository
import com.project.niyam.presentation.toPreviewScreenUIState
import com.project.niyam.presentation.toTasks
import com.project.niyam.utils.Constants
import com.project.niyam.utils.PrefUtils
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
class TaskPreviewScreenViewModel @Inject constructor(
    private val taskRepositoryImpl: TaskRepository,
    private val prefUtils: PrefUtils
) :
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

    fun updateComplete() = viewModelScope.launch {
        prefUtils.saveString(Constants.PREF_UTILS_TASK, "0")
        _uiState.value = _uiState.value.copy(isCompleted = true)
        updateStrictTask()
    }

    fun offPrefUtil() = viewModelScope.launch {
        prefUtils.saveString(Constants.PREF_UTILS_TASK, "0")
    }

    fun onPrefUtil(id: Int) = viewModelScope.launch {
        prefUtils.saveString(Constants.PREF_UTILS_TASK, id.toString())
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


    fun subTaskDone(index: Int) = viewModelScope.launch {
        val currentSubTasks = _uiState.value.subTasks.toMutableList()

        val updatedSubTask = currentSubTasks[index].copy(isCompleted = true)
        currentSubTasks[index] = updatedSubTask
        _uiState.value = _uiState.value.copy(subTasks = currentSubTasks)
        updateStrictTask()
        _uiState.value = _uiState.value.copy(subTasks = currentSubTasks)
        updateStrictTask()
    }

    private suspend fun updateStrictTask() {
        taskRepositoryImpl.updateTasks(_uiState.value.toTasks())
    }
}
