package com.project.niyam.presentation.screens.view.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.niyam.presentation.screens.viewmodels.tasks.CreateTaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTask(
    viewModel: CreateTaskViewModel,
    date: String,
    onClick: () -> Unit,
    navigateToCreateSubTaskScreen: () -> Unit,
) {
    val uiState by viewModel.uiState
    viewModel.updateDate(date)

    LazyColumn(modifier = Modifier.padding(12.dp)) {
        item {
            Row(modifier = Modifier.padding(12.dp)) {
                Text("Enter Name :")
                TextField(value = uiState.name, onValueChange = {
                    viewModel.updateName(it)
                }, modifier = Modifier.padding(start = 12.dp))
            }
            Row(modifier = Modifier.padding(12.dp)) {
                Text("Enter Description :")
                TextField(value = uiState.description, onValueChange = {
                    viewModel.updateDescription(it)
                }, modifier = Modifier.padding(start = 12.dp))
            }

            Row(modifier = Modifier.padding(12.dp)) {
                Text("Enter time in minutes :")
                TextField(value = uiState.minutesRemaining, onValueChange = {
                    viewModel.updateMinutes(it)
                }, modifier = Modifier.padding(start = 12.dp))
            }
            Row(modifier = Modifier.padding(12.dp)) {
                Text("Enter Days :")
                TextField(value = uiState.days, onValueChange = {
                    viewModel.updateDays(it)
                }, modifier = Modifier.padding(start = 12.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))

            uiState.subTasks.forEach {
                Card(colors = CardDefaults.cardColors()) {
                    Column {
                        Text("Hii")
                        Text(it.subTaskName)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(it.subTaskDescription)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(modifier = Modifier.padding(12.dp), onClick = {
                navigateToCreateSubTaskScreen()
            }) { Text("Add SubTask") }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                modifier = Modifier.padding(12.dp),
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.saveTask(date = date)
                    }
                    onClick()
                },
            ) {
                Text("Save")
            }
        }
    }
}