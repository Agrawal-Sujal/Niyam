package com.project.niyam.presentation.screens.viewmodels.preview

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.domain.model.GeneralInfo
import com.project.niyam.domain.model.SubTasks
import com.project.niyam.domain.repository.GeneralInfoRepository
import com.project.niyam.domain.repository.TaskRepository
import com.project.niyam.presentation.toPreviewScreenUIState
import com.project.niyam.presentation.toTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PreviewScreenUIState(
    val id: Int = 0,
    val taskName: String = "",
    var taskDescription: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val isCompleted: Int = 0,
    val subTasks: List<SubTasks> = listOf(SubTasks(), SubTasks()),
    val minutesRemaining: String = "",
    val currentIndex: Int = 0,
)

@HiltViewModel
class TaskPreviewScreenViewModel @Inject constructor(
    private val taskRepositoryImpl: TaskRepository,
    private val generalInfoRepository: GeneralInfoRepository,
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

    private suspend fun updateComplete() {
        generalInfoRepository.updateGeneralInfo(
            GeneralInfo(
                strictTaskRunningId = 0,
                normalTaskRunningId = 0,
            ),
        )
    }

    fun offPrefUtil() = viewModelScope.launch {
        generalInfoRepository.updateGeneralInfo(
            GeneralInfo(
                strictTaskRunningId = 0,
                normalTaskRunningId = 0,
            ),
        )
    }

    fun onPrefUtil(id: Int) = viewModelScope.launch {
        generalInfoRepository.updateGeneralInfo(
            GeneralInfo(
                strictTaskRunningId = 0,
                normalTaskRunningId = id,
            ),
        )
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

    suspend fun subTaskDone(index: Int, last: Boolean = false, secondsRemaining: String = "") {
        val currentSubTasks = _uiState.value.subTasks.toMutableList()

        val updatedSubTask = currentSubTasks[index].copy(isCompleted = true)
        currentSubTasks[index] = updatedSubTask
        if (last) {
            updateComplete()
            _uiState.value = _uiState.value.copy(
                subTasks = currentSubTasks,
                isCompleted = 1,
                minutesRemaining = secondsRemaining,
            )
        } else {
            _uiState.value = _uiState.value.copy(subTasks = currentSubTasks)
        }
        updateStrictTask()
    }

    private suspend fun updateStrictTask() {
        taskRepositoryImpl.updateTasks(_uiState.value.toTasks())
    }
}
