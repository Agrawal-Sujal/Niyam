package com.project.niyam.presentation.screens.viewmodels.preview

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.domain.model.GeneralInfo
import com.project.niyam.domain.model.SubTasks
import com.project.niyam.domain.repository.GeneralInfoRepository
import com.project.niyam.domain.repository.StrictTaskRepository
import com.project.niyam.presentation.toStrictPreviewScreenUIState
import com.project.niyam.presentation.toStrictTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StrictPreviewScreenUIState(
    val id: Int = 0,
    val taskName: String = "",
    var taskDescription: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val isCompleted: Int = 0,
    val subTasks: List<SubTasks> = listOf(SubTasks(), SubTasks()),
    val date: String = "",
    val currentIndex: Int = 0,
)

@HiltViewModel
class PreviewScreenViewModel @Inject constructor(
    private val strictTaskRepositoryImpl: StrictTaskRepository,
    @ApplicationContext val context: Context,
    private val generalInfoRepository: GeneralInfoRepository,
) :
    ViewModel() {

    private val _uiState = mutableStateOf(StrictPreviewScreenUIState())
    val uiState: State<StrictPreviewScreenUIState> = _uiState
    var id: Int = 0

    fun updateID(id: Int, context: Context) {
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

    private fun getStrictTask() {
        viewModelScope.launch {
            try {
                strictTaskRepositoryImpl.getStrictTaskById(id)
                    .collect { strictTask ->
                        _uiState.value =
                            strictTask.toStrictPreviewScreenUIState(_uiState.value.currentIndex)
                    }
                Log.i("uiState", _uiState.value.toString())
            } catch (e: Exception) {
                // Handle error (e.g., log it or show an error state in the UI)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun subTaskDone(index: Int, last: Boolean = false) {
        val currentSubTasks = _uiState.value.subTasks.toMutableList()

        val updatedSubTask = currentSubTasks[index].copy(isCompleted = true)
        currentSubTasks[index] = updatedSubTask
        if (last) {
            updateComplete()
            _uiState.value = _uiState.value.copy(subTasks = currentSubTasks, isCompleted = 1)
        } else {
            _uiState.value = _uiState.value.copy(subTasks = currentSubTasks)
        }
        updateStrictTask()
    }

    private suspend fun updateStrictTask() {
        strictTaskRepositoryImpl.updateStrictTasks(_uiState.value.toStrictTasks())
    }
}
