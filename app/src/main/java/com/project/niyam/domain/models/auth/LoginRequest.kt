package com.project.niyam.domain.models.auth

data class LoginRequest(
    val username: String,
    val password: String,
)