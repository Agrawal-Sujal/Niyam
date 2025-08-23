package com.project.niyam.domain.models

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
)
