package com.project.niyam.ui.screens.runningTask

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.domain.repository.AlarmRepository
import com.project.niyam.domain.repository.FlexibleTaskRepository
import com.project.niyam.domain.repository.SubTaskRepository
import com.project.niyam.domain.repository.TimeBoundTaskRepository
import com.project.niyam.services.local.CountdownService
import com.project.niyam.utils.TimerState
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
    private val flexibleTaskRepository: FlexibleTaskRepository,
    private val timeBoundedTaskRepository: TimeBoundTaskRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val taskId: Int = checkNotNull(savedStateHandle["taskId"])
    private val isFlexible: Boolean = savedStateHandle["isFlexible"] ?: false

    private val _uiState =
        MutableStateFlow(TaskUiState(endTime = LocalTime.now(), endDate = LocalDate.now()))
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Launch task observation in a separate coroutine to avoid blocking
            launch {
                if (isFlexible) {
                    flexibleTaskRepository.observe(taskId.toLong()).collect { task ->
                        _uiState.value = _uiState.value.copy(
                            title = task?.taskName ?: "",
                            description = task?.taskDescription ?: "",
                        )
                    }
                } else {
                    timeBoundedTaskRepository.observeTask(taskId).collect { task ->
                        _uiState.value = _uiState.value.copy(
                            title = task?.taskName ?: "",
                            description = task?.taskDescription ?: "",
                        )
                    }
                }
            }

            // Handle alarm and subtask observation
            if (!isFlexible) {
                // Get initial alarm and start if exists
                val alarm = alarmRepo.observe(taskId.toLong(), false).first()
                _uiState.value = _uiState.value.copy(id = alarm?.id)
                if (alarm != null) {
                    start()
                }
            }

            // Combine alarm and subtask data
            combine(
                alarmRepo.observe(taskId.toLong(), isFlexible),
                subTaskRepo.getAllSubTask(taskId, isFlexible),
            ) { alarm, subTasks ->
                alarm?.toUiState(subTasks) ?: TaskUiState(
                    taskId = taskId,
                    isFlexible = isFlexible,
                    endTime = LocalTime.now(),
                    endDate = LocalDate.now(),
                )
            }.collect { state ->
                // Merge with existing title and description to avoid overwriting
                _uiState.value = state.copy(
                    title = _uiState.value.title.takeIf { it.isNotEmpty() } ?: state.title,
                    description = _uiState.value.description.takeIf { it.isNotEmpty() }
                        ?: state.description,
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun pause() {
        if (_uiState.value.id != null) {
            context.startService(
                CountdownService.intent(
                    context,
                    _uiState.value.id!!,
                    CountdownService.ACTION_PAUSE,
                ),
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
    fun finalDone() {
        if (_uiState.value.id != null) {
            context.startService(
                CountdownService.intent(
                    context,
                    _uiState.value.id!!,
                    CountdownService.ACTION_FINAL_DONE,
                ),
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun start() {
        if (_uiState.value.id != null) {
            context.startForegroundService(
                CountdownService.intent(
                    context,
                    _uiState.value.id!!,
                    CountdownService.ACTION_START,
                ),
            )
        }
    }

    fun markSubTaskDone(subTaskId: Int) {
        viewModelScope.launch {
            val subTask = _uiState.value.subTasks.find { it.id == subTaskId }
            Log.d("SubTask ID", subTask.toString())
            if (subTask == null) return@launch
            subTask.isCompleted = true
            val remainingSubTasks = _uiState.value.subTasks.count { !it.isCompleted }
            if (remainingSubTasks == 0) {
                _uiState.value = _uiState.value.copy(timerState = TimerState.DONE)
            }
            subTaskRepo.updateSubTask(subTask.toEntity())

            if (remainingSubTasks == 0) {
                finalDone()
                if (isFlexible) {
                    val task = flexibleTaskRepository.getTask(taskId)
                    if (task != null) {
                        task.isCompleted = true
                        flexibleTaskRepository.updateTask(task)
                    }
                } else {
                    val task = timeBoundedTaskRepository.getTask(taskId)
                    if (task != null) {
                        task.isCompleted = true
                        timeBoundedTaskRepository.updateTask(task)
                    }
                }
            }
        }
    }
}
