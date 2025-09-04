package com.project.niyam.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.data.local.appPref.AppPref
import com.project.niyam.domain.repository.FlexibleTaskRepository
import com.project.niyam.domain.repository.SyncRepository
import com.project.niyam.domain.repository.TimeBoundTaskRepository
import com.project.niyam.utils.toUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class HomeScreenViewModel @Inject constructor(
    private val timeBoundRepo: TimeBoundTaskRepository,
    private val flexibleRepo: FlexibleTaskRepository,
    val appPref: AppPref,
    val syncRepository: SyncRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(
        HomeScreenUIState(
            selectedDate = LocalDate.now(),
            weekDates = weekFor(LocalDate.now()),
        ),
    )

    @RequiresApi(Build.VERSION_CODES.O)
    val ui: StateFlow<HomeScreenUIState> = _ui

    private var loadJob: Job? = null

    init {
        loadFor(_ui.value.selectedDate)
    }

    fun logout() {
        viewModelScope.launch {
            appPref.clearUser()
        }
    }

    fun selectDate(date: LocalDate) {
        val newWeek = weekFor(date)
        _ui.update { it.copy(selectedDate = date, weekDates = newWeek) }
        loadFor(date)
    }

    fun syncTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            val userId = appPref.userId.first()
            if (userId != null)
                syncRepository.syncTasks(userId.toInt())
        }
    }

    private fun loadFor(date: LocalDate) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                combine(
                    timeBoundRepo.getAllTask(date),
                    flexibleRepo.getAllTask(date),
                ) { strictList, flexList ->
                    val strict = strictList.map { it.toUI() }
                    val flex = flexList.map { it.toUI(date) }
                    strict to flex
                }.collectLatest { (strict, flex) ->
                    _ui.update {
                        it.copy(
                            timeBoundTasks = strict,
                            flexibleTasks = flex,
                            isLoading = false,
                        )
                    }
                }
            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun deleteTask(taskId: Int, isFlexible: Boolean) {
        if (isFlexible) {
            deleteFlexibleTask(taskId)
        } else {
            deleteTimeBoundTask(taskId)
        }
    }

    fun deleteTimeBoundTask(taskId: Int) {
        viewModelScope.launch {
            try {
                timeBoundRepo.deleteTask(taskId)
            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to delete task",
                    )
                }
            }
        }
    }

    fun deleteFlexibleTask(taskId: Int) {
        viewModelScope.launch {
            try {
                flexibleRepo.deleteTask(taskId)
            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to delete task",
                    )
                }
            }
        }
    }

    private fun weekFor(anchor: LocalDate): List<LocalDate> {
        val start = anchor.with(DayOfWeek.MONDAY)
        return (0..6).map { start.plusDays(it.toLong()) }
    }
}
