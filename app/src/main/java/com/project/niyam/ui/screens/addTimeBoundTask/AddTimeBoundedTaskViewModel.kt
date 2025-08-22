package com.project.niyam.ui.screens.addTimeBoundTask

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.data.local.entity.TimeBoundTaskEntity
import com.project.niyam.domain.repository.AlarmRepository
import com.project.niyam.domain.repository.SubTaskRepository
import com.project.niyam.domain.repository.TimeBoundTaskRepository
import com.project.niyam.utils.toAlarmEntity
import com.project.niyam.utils.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddTimeBoundedTaskViewModel @Inject constructor(
    private val timeBoundTaskRepository: TimeBoundTaskRepository,
    private val subTaskRepository: SubTaskRepository,
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun onNameChanged(new: String) = _uiState.update { it.copy(name = new) }
    fun onDescriptionChanged(new: String) = _uiState.update { it.copy(description = new) }
    fun onStartTimeSelected(time: LocalTime) = _uiState.update { it.copy(startTime = time) }
    fun onEndTimeSelected(time: LocalTime) = _uiState.update { it.copy(endTime = time) }

    fun onCancelClick() = _uiState.update { it.copy(showCancelDialog = true) }
    fun dismissCancelDialog() = _uiState.update { it.copy(showCancelDialog = false) }

    fun openSubTaskSheet() = _uiState.update { it.copy(showSubTaskSheet = true) }
    fun dismissSubTaskSheet() = _uiState.update { it.copy(showSubTaskSheet = false) }

    fun onTempSubTaskTitleChange(v: String) = _uiState.update { it.copy(tempSubTaskTitle = v) }
    fun onTempSubTaskDescriptionChange(v: String) =
        _uiState.update { it.copy(tempSubTaskDescription = v) }

    fun addSubTask(title: String, description: String) {
        onTempSubTaskTitleChange(title)
        onTempSubTaskDescriptionChange(description)
        val current = _uiState.value
        if (current.tempSubTaskTitle.isNotBlank()) {
            _uiState.update {
                it.copy(
                    subTasks = it.subTasks + SubTaskUi(
                        current.tempSubTaskTitle,
                        current.tempSubTaskDescription
                    ),
                    tempSubTaskTitle = "",
                    tempSubTaskDescription = ""
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSaveTask(onDone: () -> Unit) {
        val state = _uiState.value
        if (state.startTime == null || state.endTime == null) return
        viewModelScope.launch {


            // 1. Insert main task
            val taskId = timeBoundTaskRepository.insertTask(
                state.toEntity()
            )

            // 2. Insert all subtasks referencing this taskId
            state.subTasks.forEach { sub ->
                subTaskRepository.insertSubTask(
                    sub.toEntity(taskId.toInt(), false)
                )
            }

            alarmRepository.save(state.toAlarmEntity(taskId.toInt()))

            onDone()
        }
    }
}
