package com.project.niyam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey var id: Int = 0,
    val senderId: Int,
    val senderUsername: String,
    val receiverId: Int,
    val receiverUsername: String,
    val status: FriendStatus
)

enum class FriendStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}