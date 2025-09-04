package com.project.niyam.data.repository

import com.project.niyam.domain.models.auth.AuthResponse
import com.project.niyam.domain.models.auth.LoginRequest
import com.project.niyam.domain.models.auth.RegisterRequest
import com.project.niyam.domain.repository.AuthRepository
import com.project.niyam.services.remote.AuthServices
import com.project.niyam.utils.Resource
import com.project.niyam.utils.Utils.parseResponse
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    val services: AuthServices,
) : AuthRepository {
    override suspend fun login(loginRequest: LoginRequest): Resource<AuthResponse> {
        val response = services.login(loginRequest)
        return parseResponse(response)
    }

    override suspend fun register(registerRequest: RegisterRequest): Resource<AuthResponse> {
        val response = services.register(registerRequest)
        return parseResponse(response)
    }
}
