package com.project.niyam.presentation.screens.view.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.niyam.R
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
    navigateToCreateSubTaskScreen: (String) -> Unit,
    id: String,
) {
    val uiState by viewModel.uiState
    viewModel.updateDate(date)
    if (id != "-1") {
        LaunchedEffect(key1 = id) {
            viewModel.loadSTask(id.toInt())
        }
    }
    LazyColumn(modifier = Modifier.padding(12.dp)) {
        item {
            TextField(
                value = uiState.name,
                onValueChange = { viewModel.updateName(it) },
                label = "Task Name",
            )
            TextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = "Task Description",
            )
            TextField(
                value = uiState.minutesRemaining,
                onValueChange = { viewModel.updateMinutes(it) },
                label = "Time in minutes",
                keyboardType = KeyboardType.Number,
            )
            TextField(
                value = uiState.days,
                onValueChange = { viewModel.updateDays(it) },
                label = "Days to complete",
                keyboardType = KeyboardType.Number,
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Sub Tasks",
                    color = colorResource(R.color.PrimaryColorText),
                    fontSize = 20.sp,
                )
                FloatingActionButton(
                    onClick = {
                        navigateToCreateSubTaskScreen("-1")
                    },
                    containerColor = colorResource(R.color.PrimaryColor),
                    modifier = Modifier.padding(end = 12.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            uiState.subTasks.forEachIndexed { it1, it ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    SubTask(
                        name = it.subTaskName,
                        description = it.subTaskDescription,
                        onDelete = { viewModel.removeSubTask(it) },
                        onEdit = { navigateToCreateSubTaskScreen(it1.toString()) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier
                        .padding(12.dp),
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            if (id == "-1") {
                                viewModel.saveTask()
                            } else {
                                viewModel.updateTask(id = id.toInt())
                            }
                        }
                        onClick()
                    },
                    colors = ButtonColors(
                        containerColor = colorResource(R.color.PrimaryColorText),
                        contentColor = colorResource(R.color.NormalText),
                        disabledContainerColor = colorResource(R.color.PrimaryColorText),
                        disabledContentColor = colorResource(R.color.PrimaryColorText),
                    ),
                ) {
                    if (id == "-1") {
                        Text("Save", color = Color.Black, fontSize = 20.sp)
                    } else {
                        Text("Update", color = Color.Black, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}
