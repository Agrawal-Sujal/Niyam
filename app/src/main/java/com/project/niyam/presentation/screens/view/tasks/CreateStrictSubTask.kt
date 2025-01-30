package com.project.niyam.presentation.screens.view.tasks

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.niyam.presentation.screens.viewmodels.tasks.CreateStrictTaskViewModel
import com.project.niyam.presentation.screens.viewmodels.tasks.CreateTaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun CreateStrictSubTask(
    viewModel: CreateStrictTaskViewModel,
    navigateToCreateTaskScreen: () -> Unit,
    idx: Int,
    id: Int
) {
    val uiState by viewModel.uiStateSubTask
    if (idx != -1) {
        LaunchedEffect(key1 = idx) {
            Log.d(
                "CreateStrictSubTask", " Loading Strict Sub Task$idx"
            )
            viewModel.loadStrictSubTask(idx)
        }
    }

    Column {
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = uiState.subTaskName,
            onValueChange = { viewModel.updateSubTaskName(it) },
            label = "Sub Task Name"
        )
        Spacer(modifier = Modifier.height(12.dp))
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
//                        if (id == -1)
                        viewModel.saveSubTask()
//                        else viewModel.saveSubTaskWithId()
                        navigateToCreateTaskScreen()
                    }, modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .weight(5f)
                ) { Text("Done") }
                Button(
                    onClick = {
//                        CoroutineScope(Dispatchers.IO).launch {
//                        if (id == -1)
                        viewModel.saveSubTask()
//                        else viewModel.saveSubTaskWithId()
//                        }
                    }, modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .weight(5f)
                        .padding(start = 4.dp)
                ) { Text("Add More") }
            } else {
                Button(onClick = {
                    runBlocking {
//                        if (id == -1)
                        viewModel.updateSubTask(idx)
//                        else
//                            viewModel.updateSubTaskWithId(idx)
                        navigateToCreateTaskScreen()
                    }
                }, modifier = Modifier.fillMaxWidth(0.35f)) {
                    Text("Update")
                }
            }
        }
    }
}
