package com.project.niyam.ui.screens.friends

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.niyam.data.local.appPref.AppPref
import com.project.niyam.data.local.entity.FriendEntity
import com.project.niyam.data.local.entity.FriendStatus
import com.project.niyam.domain.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendRepository,
    private val appPref: AppPref
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendUiState())
    val uiState: StateFlow<FriendUiState> = _uiState.asStateFlow()

    init {
        loadFriends()
    }

    fun loadFriends() {
        viewModelScope.launch {
            try {
                repository.getAllFriends()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.observeFriends().collectLatest { friends ->

                    val userId = appPref.userId.first()?.toInt() ?: -1
                    val incomingRequests =
                        friends.filter { it.receiverId == userId && it.status == FriendStatus.PENDING }
                    val pendingRequests =
                        friends.filter { it.senderId == userId && it.status != FriendStatus.ACCEPTED }
                    val acceptedFriends =
                        friends.filter { it.senderId == userId && it.status == FriendStatus.ACCEPTED }


                    val followBack =
                        friends.filter { relation ->
                            // condition 1: I'm the receiver
                            relation.receiverId == userId &&
                                    // condition 2: already accepted
                                    relation.status == FriendStatus.ACCEPTED &&
                                    // condition 3: I haven't followed them back
                                    friends.none { reverse ->
                                        reverse.senderId == userId &&
                                                reverse.receiverId == relation.senderId
                                    }

                        }

                    Log.d("Follow Back", followBack.toString())

                    _uiState.value =
                        _uiState.value.copy(
                            incomingRequests = incomingRequests.map { it.toUi() },
                            pendingRequests = pendingRequests.map { it.toUi() },
                            friends = acceptedFriends.map { it.toUi() },
                            followBack = followBack.map { it.toUi() }, isLoading = false
                        )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(suggestions = emptyList())
            return
        }

        viewModelScope.launch {
//            try {
//                val results = repository.searchUsers(query)
//                _uiState.value = _uiState.value.copy(suggestions = results)
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(error = e.message)
//            }
        }
    }

    fun addFriend(username: String) {
        viewModelScope.launch {
//            try {
//                val currentUserId = appPref.userId.first()!!.toInt()
//                val success = repository.addFriend(currentUserId, username)
//                if (success) {
//                    _uiState.value = _uiState.value.copy(
//                        searchQuery = "",
//                        suggestions = emptyList()
//                    )
//                    loadFriends() // refresh lists
//                }
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(error = e.message)
//            }
        }
    }

    fun acceptRequest(requestId: Int) {
        viewModelScope.launch {
//            try {
//                repository.acceptRequest(requestId)
//                loadFriends()
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(error = e.message)
//            }
        }
    }

    fun rejectRequest(requestId: Int) {
        viewModelScope.launch {
//            try {
//                repository.rejectRequest(requestId)
//                loadFriends()
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(error = e.message)
//            }
        }
    }

    fun withdrawRequest(friendItemUi: FriendItemUi) {

    }

    fun onUnfollow(friendId: Int) {

    }

    fun onFollowBack(friendItemUi: FriendItemUi) {
        val temp = friendItemUi
        friendItemUi.receiverId = temp.senderId
        friendItemUi.senderId = temp.receiverId
        friendItemUi.receiverUsername = temp.senderUsername
        friendItemUi.senderUsername = temp.receiverUsername

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.addFriend(friendItemUi.toEntity())
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
