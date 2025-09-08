package com.project.niyam.ui.screens.friends

import com.project.niyam.data.local.entity.FriendEntity
import com.project.niyam.data.local.entity.FriendStatus

data class FriendUiState(
    val incomingRequests: List<FriendItemUi> = emptyList(),
    val pendingRequests: List<FriendItemUi> = emptyList(),
    val friends: List<FriendItemUi> = emptyList(),
    val followBack: List<FriendItemUi> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class FriendItemUi(
    val id: Int,
    var senderId: Int,
    var senderUsername: String,
    var receiverId: Int,
    var receiverUsername: String,
    val status: FriendStatus,
)

// Mapper
fun FriendEntity.toUi(): FriendItemUi {
    return FriendItemUi(
        id = id,
        senderId = senderId,
        senderUsername = senderUsername,
        receiverId = receiverId,
        receiverUsername = receiverUsername,
        status = status,
    )
}
fun FriendItemUi.toEntity(): FriendEntity {
    return FriendEntity(
        senderId = senderId,
        senderUsername = senderUsername,
        receiverId = receiverId,
        receiverUsername = receiverUsername,
        status = status,
    )
}
