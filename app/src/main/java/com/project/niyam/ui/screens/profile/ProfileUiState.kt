package com.project.niyam.ui.screens.profile

data class ProfileUiState(
    val photoUrl: String? = null,
    val username: String = "",
    val email: String = "",
    val isSyncing: Boolean = false
)
