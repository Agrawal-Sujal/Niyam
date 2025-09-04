package com.project.niyam.domain.models.auth

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
)