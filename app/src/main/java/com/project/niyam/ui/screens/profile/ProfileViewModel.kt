package com.project.niyam.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.data.local.appPref.AppPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val appPref: AppPref
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ProfileUiState()
    )
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        viewModelScope.launch {
            val userName = appPref.username.first() ?: ""
            val email = appPref.email.first() ?: "your email"
            _uiState.value = _uiState.value.copy(username = userName, email = email)
        }
    }

    fun sync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            delay(2000) // fake sync
            _uiState.value = _uiState.value.copy(isSyncing = false)
        }
    }

    fun logout() {
        // clear tokens, navigate to login
    }
}
