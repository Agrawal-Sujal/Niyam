package com.project.niyam.domain.models.auth

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

@Immutable
data class AuthResponse(
    val message: String,
    @SerializedName("user_id") val userId: Int,
    val username: String,
    val token: String,
)
