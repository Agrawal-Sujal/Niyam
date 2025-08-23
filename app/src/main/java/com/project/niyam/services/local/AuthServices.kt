package com.project.niyam.services.local

import com.project.niyam.domain.models.AuthResponse
import com.project.niyam.domain.models.LoginRequest
import com.project.niyam.domain.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthServices {

    @POST("users/login/")
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): Response<AuthResponse>

    @POST("users/register/")
    suspend fun register(
        @Body registerRequest: RegisterRequest,
    ): Response<AuthResponse>
}
