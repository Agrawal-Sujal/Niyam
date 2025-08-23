package com.project.niyam.ui.screens.addTimeBoundTask

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTimeBoundTaskScreen(
    onBack: () -> Unit,
    onTaskAdded: () -> Unit,
    viewModel: AddTimeBoundedTaskViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), // same as default TopAppBar height
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add Time Bounded Task",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.onNameChanged(it) },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = uiState.description,
            onValueChange = { viewModel.onDescriptionChanged(it) },
            label = { Text("Task Description") },
            modifier = Modifier.fillMaxWidth(),
        )

        // Start Time
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Start Time: ")
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = { showStartPicker = true },
            ) {
                Text(
                    uiState.startTime?.format(DateTimeFormatter.ofPattern("hh:mm a"))
                        ?: "Select",
                )
            }
        }

        // End Time
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "End Time: ")
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = { showEndPicker = true },
            ) {
                Text(
                    uiState.endTime?.format(DateTimeFormatter.ofPattern("hh:mm a"))
                        ?: "Select",
                )
            }
        }

        // Subtasks
        Text("Subtasks:", style = MaterialTheme.typography.titleMedium)
        uiState.subTasks.forEach {
            Text("• ${it.title} - ${it.description}")
        }
        OutlinedButton(
            onClick = { viewModel.openSubTaskSheet() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Add Subtask")
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            OutlinedButton(onClick = { viewModel.onCancelClick() }) {
                Text("Cancel")
            }
            Button(onClick = {
                viewModel.onSaveTask(onDone = {
                    onBack()
                })
            }) {
                Text("Add")
            }
        }
    }

    // Start time picker
    if (showStartPicker) {
        TimePickerDialog(
            onDismissRequest = { showStartPicker = false },
            onTimeChange = { hour, minute ->
                viewModel.onStartTimeSelected(LocalTime.of(hour, minute))
                showStartPicker = false
            },
        )
    }

    // End time picker
    if (showEndPicker) {
        TimePickerDialog(
            onDismissRequest = { showEndPicker = false },
            onTimeChange = { hour, minute ->
                viewModel.onEndTimeSelected(LocalTime.of(hour, minute))
                showEndPicker = false
            },
        )
    }

    // Cancel confirmation
    if (uiState.showCancelDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissCancelDialog() },
            confirmButton = {
                TextButton(onClick = onBack) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissCancelDialog() }) { Text("No") }
            },
            title = { Text("Discard Task?") },
            text = { Text("Are you sure you want to discard this task?") },
        )
    }

    // Subtask bottom sheet
    if (uiState.showSubTaskSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissSubTaskSheet() },
        ) {
            SubTaskInputSheet(
                onDone = { title, desc ->
                    viewModel.addSubTask(title, desc)
                    viewModel.dismissSubTaskSheet()
                },
                onAddMore = { title, desc ->
                    viewModel.addSubTask(title, desc)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
) {
    val state = rememberTimePickerState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onTimeChange(state.hour, state.minute)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        },
        text = {
            TimePicker(state = state)
        },
    )
}

@Composable
fun SubTaskInputSheet(
    onDone: (String, String) -> Unit,
    onAddMore: (String, String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "Add Subtask",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            OutlinedButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onDone(title, description)
                        title = ""
                        description = ""
                    }
                },
            ) {
                Text("Done")
            }

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAddMore(title, description)
                        title = ""
                        description = ""
                    }
                },
            ) {
                Text("Add More")
            }
        }
    }
}
