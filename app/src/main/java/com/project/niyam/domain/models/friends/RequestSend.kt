package com.project.niyam.domain.models.friends

import com.google.gson.annotations.SerializedName

data class RequestSend(
    @SerializedName("friend_id") val friendId: Int
)
