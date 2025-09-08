package com.project.niyam.domain.repository

import com.project.niyam.data.local.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    suspend fun getAllFriends()

    fun observeFriends(): Flow<List<FriendEntity>>
    suspend fun searchUsers(query: String): List<String>
    suspend fun addFriend(
        friendEntity: FriendEntity,
    ): Result<Unit>

    suspend fun acceptRequest(requestId: Int)
    suspend fun rejectRequest(requestId: Int)
}
