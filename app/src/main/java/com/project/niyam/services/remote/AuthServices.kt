package com.project.niyam.services.remote

import com.project.niyam.domain.models.auth.AuthResponse
import com.project.niyam.domain.models.auth.LoginRequest
import com.project.niyam.domain.models.auth.RegisterRequest
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
