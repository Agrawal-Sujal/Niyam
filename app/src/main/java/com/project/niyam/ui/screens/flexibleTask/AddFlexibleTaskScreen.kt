package com.project.niyam.ui.screens.flexibleTask

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.ui.screens.addTimeBoundTask.SubTaskInputSheet
import com.project.niyam.ui.screens.addTimeBoundTask.TimePickerDialog
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlexibleTaskScreen(
    onBack: () -> Unit,
    viewModel: AddFlexibleTaskViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Date pickers require initial values
    val today = LocalDate.now()

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Top AppBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Flexible Task", style = MaterialTheme.typography.titleLarge)
        }

        // Task Name & Description
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

        // Window Start Date
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Start Date: ")
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { showStartDatePicker = true }) {
                Text(uiState.windowStartDate?.toString() ?: "Select")
            }
        }

        // Window End Date
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("End Date: ")
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { showEndDatePicker = true }) {
                Text(uiState.windowEndDate?.toString() ?: "Select")
            }
        }

        // Window Start & End Time (reuse TimePickerDialog from previous code)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Start Time: ")
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { showStartTimePicker = true }) {
                Text(uiState.windowStartTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Select")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("End Time: ")
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { showEndTimePicker = true }) {
                Text(uiState.windowEndTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Select")
            }
        }

        // Hours Alloted
        OutlinedTextField(
            value = if (uiState.hoursAlloted > 0) uiState.hoursAlloted.toString() else "",
            onValueChange = { viewModel.onHoursAllotedChanged(it.toIntOrNull() ?: 1) },
            label = { Text("Hours Alloted") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        // Subtasks
        Text("Subtasks:", style = MaterialTheme.typography.titleMedium)
        uiState.subTasks.forEach { Text("• ${it.title} - ${it.description}") }

        OutlinedButton(
            onClick = { viewModel.openSubTaskSheet() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Add Subtask")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            OutlinedButton(onClick = { viewModel.onCancelClick() }) { Text("Cancel") }
            Button(onClick = { viewModel.onSaveTask { onBack() } }) { Text("Add") }
        }
    }

    // Start Date Picker
    if (showStartDatePicker) {
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                viewModel.onWindowStartDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
                showStartDatePicker = false
            },
            today.year,
            today.monthValue - 1,
            today.dayOfMonth,
        )
        datePickerDialog.show()
    }

    // End Date Picker
    if (showEndDatePicker) {
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                viewModel.onWindowEndDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
                showEndDatePicker = false
            },
            today.year,
            today.monthValue - 1,
            today.dayOfMonth,
        )
        datePickerDialog.show()
    }

    // Start time picker
    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            onTimeChange = { hour, minute ->
                viewModel.onWindowStartTimeSelected(LocalTime.of(hour, minute))
                showStartTimePicker = false
            },
        )
    }

    // End time picker
    if (showEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            onTimeChange = { hour, minute ->
                viewModel.onWindowEndTimeSelected(LocalTime.of(hour, minute))
                showEndTimePicker = false
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
