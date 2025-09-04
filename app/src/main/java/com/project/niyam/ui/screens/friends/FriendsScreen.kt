package com.project.niyam.ui.screens.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FriendsScreen(viewModel: FriendsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    FriendsPage(
        state = state,
        onSearchChange = viewModel::onSearchChange,
        onAddFriend = viewModel::addFriend,
        onAccept = viewModel::acceptRequest,
        onReject = viewModel::rejectRequest,
//        onWithdraw = viewModel::withdrawRequest,
        onUnfollow = viewModel::onUnfollow,
        onFollowBack = viewModel::onFollowBack
    )
}

@Composable
fun FriendsPage(
    state: FriendUiState,
    onSearchChange: (String) -> Unit,
    onAddFriend: (String) -> Unit,
    onAccept: (Int) -> Unit,
    onReject: (Int) -> Unit,
//    onWithdraw: (Int) -> Unit,
    onUnfollow: (Int) -> Unit,
    onFollowBack: (FriendItemUi) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔍 Add Friend
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Add Friend by Username") },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = state.suggestions.isNotEmpty(),
            onDismissRequest = { }
        ) {
            state.suggestions.forEach { suggestion ->
                DropdownMenuItem(
                    text = { Text(suggestion) },
                    onClick = { onAddFriend(suggestion) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 📩 Incoming Requests
        Text("Incoming Requests", style = MaterialTheme.typography.titleMedium)
        state.incomingRequests.forEach { req ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(req.senderUsername)
                Row {
                    TextButton(onClick = { onAccept(req.id) }) { Text("Accept") }
                    TextButton(onClick = { onReject(req.id) }) { Text("Reject") }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ⏳ Pending Requests
        Text("Pending Requests", style = MaterialTheme.typography.titleMedium)
        state.pendingRequests.forEach { req ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(req.receiverUsername)
                TextButton(onClick = { }) { Text("Withdraw") }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 👥 Friends
        Text("Friends", style = MaterialTheme.typography.titleMedium)
        state.friends.forEach { friend ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(friend.receiverUsername)
                    TextButton(onClick = { onUnfollow(friend.id) }) { Text("Unfollow") }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 🔄 Follow Back
        Text("Follow Back", style = MaterialTheme.typography.titleMedium)
        state.followBack.forEach { user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(user.senderUsername)
                TextButton(onClick = { onFollowBack(user) }) { Text("Follow Back") }
            }
        }
    }
}

