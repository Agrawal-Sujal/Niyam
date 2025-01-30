package com.project.niyam.presentation.screens.view.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.niyam.presentation.screens.viewmodels.tasks.CreateTaskViewModel

@Composable
fun CreateSubTask(
    viewModel: CreateTaskViewModel,
    navigateToCreateTaskScreen: () -> Unit,
    idx: Int
) {
    val uiState by viewModel.uiStateSubTask
    if (idx != -1) {
        LaunchedEffect(key1 = idx) {
            viewModel.loadSubTask(idx)
        }
    }
    Column {
        TextField(
            value = uiState.subTaskName,
            onValueChange = { viewModel.updateSubTaskName(it) },
            label = "Sub Task Name"
        )
        TextField(
            value = uiState.subTaskDescription,
            onValueChange = { viewModel.updateSubTaskDescription(it) },
            label = "Sub Task Description"
        )
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            if (idx == -1) {
                Button(
                    onClick = {
                        viewModel.saveSubTask()
                        navigateToCreateTaskScreen()
                    }, modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .weight(5f)
                ) { Text("Done") }
                Button(
                    onClick = {
                        viewModel.saveSubTask()
                    }, modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .weight(5f)
                        .padding(start = 4.dp)
                ) { Text("Add More") }
            } else {
                Button(
                    onClick = {
                        viewModel.updateSubTask(idx)
                        navigateToCreateTaskScreen()
                    }, modifier = Modifier
                        .fillMaxWidth(0.35f)
                ) { Text("Update") }
            }
        }
    }
}