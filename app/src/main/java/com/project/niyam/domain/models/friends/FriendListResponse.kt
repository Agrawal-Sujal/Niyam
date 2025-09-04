package com.project.niyam.domain.models.friends

import com.google.gson.annotations.SerializedName


data class FriendResponse(
    val id: Int,
    @SerializedName("sender_id")val senderId : Int,
    @SerializedName("sender_username")val senderUsername : String,
    @SerializedName("receiver_id")val receiverId : Int,
    @SerializedName("receiver_username")val receiverUsername : String,
    val status: String
)
