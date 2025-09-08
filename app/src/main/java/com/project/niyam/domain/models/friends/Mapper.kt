package com.project.niyam.domain.models.friends

import com.project.niyam.data.local.entity.FriendEntity
import com.project.niyam.data.local.entity.FriendStatus

fun FriendResponse.toEntity(): FriendEntity {
    return FriendEntity(
        id = id,
        senderId = senderId,
        senderUsername = senderUsername,
        receiverId = receiverId,
        receiverUsername = receiverUsername,
        status = when (status) {
            "PENDING" -> FriendStatus.PENDING
            "ACCEPTED" -> FriendStatus.ACCEPTED
            "REJECTED" -> FriendStatus.REJECTED
            else -> FriendStatus.PENDING
        },
    )
}
