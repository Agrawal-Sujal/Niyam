package com.project.niyam.domain.repository

import com.project.niyam.domain.models.auth.AuthResponse
import com.project.niyam.domain.models.auth.LoginRequest
import com.project.niyam.domain.models.auth.RegisterRequest
import com.project.niyam.utils.Resource

interface AuthRepository {

    suspend fun login(loginRequest: LoginRequest): Resource<AuthResponse>

    suspend fun register(registerRequest: RegisterRequest): Resource<AuthResponse>
}
