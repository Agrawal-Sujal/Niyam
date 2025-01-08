package com.project.niyam.presentation.screens.viewmodels.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.domain.model.StrictTasks
import com.project.niyam.domain.repository.StrictTaskRepository
import com.project.niyam.utils.DateDetail
import com.project.niyam.utils.DateTimeDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

data class TasksHomeScreenUIState(
    val date: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TasksHomeScreenViewModel @Inject constructor(private val strictTaskRepository: StrictTaskRepository) :
    ViewModel() {

    private val _uiState = mutableStateOf(TasksHomeScreenUIState())
    val uiState: State<TasksHomeScreenUIState> = _uiState

    init {
        val currentDate = LocalDate.now()
        val startDate = currentDate.minusDays(3)
        val endDate = currentDate.plusDays(3)
        _uiState.value = _uiState.value.copy(
            date = DateTimeDetail.FULL_DATE.getDetail(),
            startDate = startDate,
            endDate = endDate
        )
        println(DateDetail.FULL_DATE.getDetail(startDate))
        println(DateDetail.FULL_DATE.getDetail(endDate))
    }

    fun changeDate(date: String) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun giveDateLists(): List<LocalDate> {
        val dateList: MutableList<LocalDate> = mutableListOf()
        var startDate = _uiState.value.startDate
        val endDate = _uiState.value.endDate
        while (startDate?.isAfter(endDate!!) == false) {
            dateList.add(startDate)
            startDate = startDate.plusDays(1)
            println(DateDetail.FULL_DATE.getDetail(startDate))
        }
        return dateList
    }

    fun increaseDate() {
        var startDate = _uiState.value.startDate
        startDate = startDate?.plusDays(7)
        var endDate = _uiState.value.endDate
        endDate = endDate?.plusDays(7)
        _uiState.value = _uiState.value.copy(startDate = startDate, endDate = endDate)
    }

    fun decreaseDate() {
        var startDate = _uiState.value.startDate
        startDate = startDate?.minusDays(7)
        var endDate = _uiState.value.endDate
        endDate = endDate?.minusDays(7)
        _uiState.value = _uiState.value.copy(startDate = startDate, endDate = endDate)
    }

    fun getAllStrictTaskRepository(date: String): StateFlow<List<StrictTasks>> {
        return strictTaskRepository.getAllStrictTasks(date)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }


}