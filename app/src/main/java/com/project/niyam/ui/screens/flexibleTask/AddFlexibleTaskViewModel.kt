package com.project.niyam.ui.screens.flexibleTask

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.domain.repository.AlarmRepository
import com.project.niyam.domain.repository.FlexibleTaskRepository
import com.project.niyam.domain.repository.SubTaskRepository
import com.project.niyam.ui.screens.addTimeBoundTask.SubTaskUi
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
class AddFlexibleTaskViewModel @Inject constructor(
    private val flexibleTaskRepository: FlexibleTaskRepository,
    private val subTaskRepository: SubTaskRepository,
    private val alarmRepository: AlarmRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlexibleUiState())
    val uiState: StateFlow<FlexibleUiState> = _uiState

    fun onNameChanged(new: String) = _uiState.update { it.copy(name = new) }
    fun onDescriptionChanged(new: String) = _uiState.update { it.copy(description = new) }
    fun onWindowStartDateSelected(date: LocalDate) =
        _uiState.update { it.copy(windowStartDate = date) }

    fun onWindowEndDateSelected(date: LocalDate) = _uiState.update { it.copy(windowEndDate = date) }
    fun onWindowStartTimeSelected(time: LocalTime) =
        _uiState.update { it.copy(windowStartTime = time) }

    fun onWindowEndTimeSelected(time: LocalTime) = _uiState.update { it.copy(windowEndTime = time) }
    fun onHoursAllotedChanged(hours: Int) = _uiState.update { it.copy(hoursAlloted = hours) }

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
                        current.tempSubTaskDescription,
                    ),
                    tempSubTaskTitle = "",
                    tempSubTaskDescription = "",
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSaveTask(onDone: () -> Unit) {
        val state = _uiState.value
        if (state.windowStartTime == null || state.windowEndTime == null ||
            state.windowStartDate == null || state.windowEndDate == null
        ) {
            return
        }

        viewModelScope.launch {
            // 1. Insert main flexible task
            val taskId = flexibleTaskRepository.insertTask(
                _uiState.value.toEntity(),
            )

            // 2. Insert all subtasks referencing this taskId
            state.subTasks.forEach { sub ->
                subTaskRepository.insertSubTask(
                    sub.toEntity(taskId.toInt(), true),
                )
            }

            alarmRepository.save(
                _uiState.value.toAlarmEntity(taskId.toInt()),
            )

            onDone()
        }
    }
}
