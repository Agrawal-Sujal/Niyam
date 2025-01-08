package com.project.niyam.presentation.screens.view.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.presentation.screens.viewmodels.tasks.CreateTaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun CreateSubTask(
    viewModel: CreateTaskViewModel,
    navigateToCreateTaskScreen: () -> Unit
) {
    val uiState by viewModel.uiStateSubTask
    Column {
        Row(modifier = Modifier.padding(12.dp)) {
            Text("Enter SubTask Name :")
            TextField(value = uiState.subTaskName, onValueChange = {
                viewModel.updateSubTaskName(it)
            }, modifier = Modifier.padding(start = 12.dp))
        }
        Row(modifier = Modifier.padding(12.dp)) {
            Text("Enter SubTask Description :")
            TextField(value = uiState.subTaskDescription, onValueChange = {
                viewModel.updateSubTaskDescription(it)
            }, modifier = Modifier.padding(start = 12.dp))
        }

        Row(modifier = Modifier.padding(12.dp)) {
            Button(onClick = {
                viewModel.saveSubTask()
                navigateToCreateTaskScreen()

            }) { Text("Done") }
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.saveSubTask()
                }
            }) { Text("Add More") }
        }
    }
}