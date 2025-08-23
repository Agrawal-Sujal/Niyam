package com.project.niyam.ui.screens.runningTask

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.data.local.entity.SubTaskEntity
import com.project.niyam.domain.repository.AlarmRepository
import com.project.niyam.domain.repository.SubTaskRepository
import com.project.niyam.services.local.CountdownService
import com.project.niyam.utils.toEntity
import com.project.niyam.utils.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmRepo: AlarmRepository,
    private val subTaskRepo: SubTaskRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val taskId: Int = checkNotNull(savedStateHandle["taskId"])
    private val isFlexible: Boolean = savedStateHandle["isFlexible"] ?: false

    private val _uiState =
        MutableStateFlow(TaskUiState(endTime = LocalTime.now(), endDate = LocalDate.now()))
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {

        viewModelScope.launch {
            if (!isFlexible) {
                val alarm = alarmRepo.observe(taskId.toLong(), isFlexible).first()
                _uiState.value = _uiState.value.copy(id = alarm?.id)
                start()
            }
            combine(
                alarmRepo.observe(taskId.toLong(), isFlexible),
                subTaskRepo.getAllSubTask(taskId),
            ) { alarm, subTasks ->
                alarm?.toUiState(subTasks) ?: TaskUiState(
                    taskId = taskId, isFlexible = isFlexible,
                    endTime = LocalTime.now(), endDate = LocalDate.now(),
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun pause() {
        if (_uiState.value.id != null) {
            context.startService(
                CountdownService.intent(context, _uiState.value.id!!, CountdownService.ACTION_PAUSE),
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun resume() {
        if (_uiState.value.id != null) {
            context.startForegroundService(
                CountdownService.intent(
                    context,
                    _uiState.value.id!!,
                    CountdownService.ACTION_RESUME,
                ),
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun done() {
        if (_uiState.value.id != null) {
            context.startService(
                CountdownService.intent(context, _uiState.value.id!!, CountdownService.ACTION_DONE),
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun start() {
        if (_uiState.value.id != null) {
            context.startForegroundService(
                CountdownService.intent(context, _uiState.value.id!!, CountdownService.ACTION_START),
            )
        }
    }

    fun markSubTaskDone(subTaskId: Int) {
        viewModelScope.launch {
            val subTask = _uiState.value.subTasks.find { it.id == subTaskId }
            subTaskRepo.updateSubTask(subTask?.toEntity() ?: SubTaskEntity())
        }
    }
}
