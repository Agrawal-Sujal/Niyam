package com.project.niyam.data.repository

import com.project.niyam.data.local.dao.FriendDao
import com.project.niyam.data.local.entity.FriendEntity
import com.project.niyam.domain.models.friends.RequestSend
import com.project.niyam.domain.models.friends.toEntity
import com.project.niyam.domain.repository.FriendRepository
import com.project.niyam.services.remote.FriendServices
import com.project.niyam.utils.Utils.parseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    val friendServices: FriendServices,
    val friendDao: FriendDao
) : FriendRepository {
    override suspend fun getAllFriends() {
        val response = friendServices.getAllFriends()
        val parseResponse = parseResponse(response)
        if (parseResponse.isSuccess) {
            val friends = parseResponse.data!!.map { it.toEntity() }
            for (friend in friends)
                friendDao.insertFriend(friend)
        }
    }

    override fun observeFriends(): Flow<List<FriendEntity>> {
        return friendDao.getAllFriends()
    }

    override suspend fun searchUsers(query: String): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun addFriend(
        friendEntity: FriendEntity
    ): Result<Unit> {
        val response = friendServices.addFriend(RequestSend(friendId = friendEntity.receiverId))
        val parseResponse = parseResponse(response)
        if (parseResponse.isSuccess) {
            val data = parseResponse.data!!
            friendEntity.id = data.id
            friendDao.insertFriend(friendEntity)
            return Result.success(Unit)
        } else {
            val error = parseResponse.error
            return Result.failure(Exception(error?.error ?: "Something went wrong"))
        }
    }

    override suspend fun acceptRequest(requestId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun rejectRequest(requestId: Int) {
        TODO("Not yet implemented")
    }
}