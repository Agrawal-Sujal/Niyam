package com.project.niyam.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.data.local.appPref.AppPref
import com.project.niyam.domain.models.auth.LoginRequest
import com.project.niyam.domain.models.auth.RegisterRequest
import com.project.niyam.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val username: String = "",
    val isLoggedIn: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appPref: AppPref,
    val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            if (username.isNotBlank() && password.isNotBlank()) {
                val response =
                    authRepository.login(LoginRequest(username = username, password = password))
                if (response.isFailed) {
                    val errorResponse = response.error
                    if (errorResponse != null) {
                        _uiState.value = _uiState.value.copy(error = errorResponse.error)
                    } else {
                        _uiState.value = _uiState.value.copy(error = "Something went wrong")
                    }
                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        val userId = data.userId
                        val username = data.username
                        val token = data.token
                        appPref.saveUser(userId.toString(), username, token)
                        _uiState.value = AuthUiState(username, true)
                    } else {
                        _uiState.value = _uiState.value.copy(error = "Something went wrong")
                    }
                }
            } else {
                _uiState.value = _uiState.value.copy(error = "Invalid credentials")
            }
        }
    }

    fun register(username: String, password: String, email: String) {
        viewModelScope.launch {
            val response =
                authRepository.register(RegisterRequest(username = username, password = password, email = email))
            if (response.isFailed) {
                val errorResponse = response.error
                if (errorResponse != null) {
                    _uiState.value = _uiState.value.copy(error = errorResponse.error)
                } else {
                    _uiState.value = _uiState.value.copy(error = "Something went wrong")
                }
            }
            if (response.isSuccess) {
                val data = response.data
                if (data != null) {
                    val userId = data.userId
                    val username = data.username
                    val token = data.token
                    appPref.saveUser(userId.toString(), username, token)
                    _uiState.value = AuthUiState(username, true)
                } else {
                    _uiState.value = _uiState.value.copy(error = "Something went wrong")
                }
            }
        }
    }
}
