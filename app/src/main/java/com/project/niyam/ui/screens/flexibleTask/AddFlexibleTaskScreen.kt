package com.project.niyam.ui.screens.flexibleTask

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.ui.screens.addTimeBoundTask.SubTaskUi
import com.project.niyam.ui.theme.NiyamColors
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlexibleTaskScreen(
    viewModel: AddFlexibleTaskViewModel=hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    // Date picker states
    var showEndDatePicker by remember { mutableStateOf(false) }

    // String states for direct text input
    var endDateString by remember { mutableStateOf("") }
    var hoursAllocatedString by remember { mutableStateOf(uiState.hoursAlloted.toString()) }

    // Update string states when values change
    LaunchedEffect(uiState.windowEndDate) {
        uiState.windowEndDate?.let {
            endDateString = it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    }

    LaunchedEffect(uiState.hoursAlloted) {
        hoursAllocatedString = uiState.hoursAlloted.toString()
    }

    // Set default values on first composition
    LaunchedEffect(Unit) {
        // Set start date to today
        viewModel.onWindowStartDateSelected(LocalDate.now())
        // Set start time to current time
        viewModel.onWindowStartTimeSelected(LocalTime.now())
        // Set end time to 24:00 (which is 00:00 of next day, but we'll use 23:59)
        viewModel.onWindowEndTimeSelected(LocalTime.of(23, 59))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NiyamColors.backgroundColor)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "Create New Task",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Title Field
            Text(
                text = "Title",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                placeholder = {
                    Text(
                        text = "Enter task title",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8A2BE2),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF8A2BE2)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Description Field
            Text(
                text = "Description",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChanged,
                placeholder = {
                    Text(
                        text = "Enter task description",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8A2BE2),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF8A2BE2)
                ),
                shape = RoundedCornerShape(8.dp),
                maxLines = 4
            )

            // End Date
            Text(
                text = "Due Date",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = endDateString,
                onValueChange = { input ->
                    endDateString = input
                    // Try to parse the input and update the ViewModel
                    if (input.matches(Regex("^([0-2][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$"))) {
                        try {
                            val date = LocalDate.parse(input, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            viewModel.onWindowEndDateSelected(date)
                        } catch (e: Exception) {
                            // Invalid date format, keep the string but don't update ViewModel
                        }
                    }
                },
                placeholder = {
                    Text(
                        text = "dd/MM/yyyy",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8A2BE2),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF8A2BE2)
                ),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date",
                        tint = Color.Gray,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current,
                        ) { showEndDatePicker = true }
                    )
                }
            )

            // Hours Allocated
            Text(
                text = "Time Estimate (hours)",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = hoursAllocatedString,
                onValueChange = { input ->
                    hoursAllocatedString = input
                    // Try to parse the input and update the ViewModel
                    input.toIntOrNull()?.let { hours ->
                        if (hours > 0 && hours <= 24) {
                            viewModel.onHoursAllotedChanged(hours)
                        }
                    }
                },
                placeholder = {
                    Text(
                        text = "e.g. 3",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8A2BE2),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF8A2BE2)
                ),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Subtasks Section
            Text(
                text = "Subtasks",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Display existing subtasks
            uiState.subTasks.forEach { subtask ->
                SubTaskItem(
                    subtask = subtask,
                    onRemove = { /* TODO: Implement remove functionality */ }
                )
            }

            // Add Subtask Button
            OutlinedButton(
                onClick = { viewModel.openSubTaskSheet() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF8A2BE2)
                ),
                border = BorderStroke(1.dp, Color(0xFF8A2BE2)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add subtask",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add another subtask")
            }

            // Create Task Button
            Button(
                onClick = {
                    viewModel.onSaveTask(onNavigateBack)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8A2BE2)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Create Task",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // Date Picker
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismiss = { showEndDatePicker = false },
            onDateSelected = { date ->
                viewModel.onWindowEndDateSelected(date)
                showEndDatePicker = false
            }
        )
    }

    // Subtask Bottom Sheet
    if (uiState.showSubTaskSheet) {
        SubTaskBottomSheet(
            tempTitle = uiState.tempSubTaskTitle,
            tempDescription = uiState.tempSubTaskDescription,
            onTitleChange = viewModel::onTempSubTaskTitleChange,
            onDescriptionChange = viewModel::onTempSubTaskDescriptionChange,
            onAddSubTask = { title, description ->
                viewModel.addSubTask(title, description)
                viewModel.dismissSubTaskSheet()
            },
            onDismiss = { viewModel.dismissSubTaskSheet() }
        )
    }

    // Cancel Dialog
    if (uiState.showCancelDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissCancelDialog() },
            title = {
                Text(
                    text = "Cancel Task Creation",
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to cancel? All unsaved changes will be lost.",
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.dismissCancelDialog()
                        onNavigateBack()
                    }
                ) {
                    Text(
                        text = "Yes, Cancel",
                        color = Color(0xFF8A2BE2)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissCancelDialog() }
                ) {
                    Text(
                        text = "Continue Editing",
                        color = Color.Gray
                    )
                }
            },
            containerColor = NiyamColors.surfaceBackgroundColor
        )
    }
}

@Composable
fun SubTaskItem(
    subtask: SubTaskUi,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = NiyamColors.surfaceBackgroundColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = subtask.title,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                if (subtask.description.isNotBlank()) {
                    Text(
                        text = subtask.description,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove subtask",
                    tint = Color(0xFFFF4444),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubTaskBottomSheet(
    tempTitle: String,
    tempDescription: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddSubTask: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = NiyamColors.surfaceBackgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Add Subtask",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = tempTitle,
                onValueChange = onTitleChange,
                placeholder = {
                    Text(
                        text = "Subtask title",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8A2BE2),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF8A2BE2)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = tempDescription,
                onValueChange = onDescriptionChange,
                placeholder = {
                    Text(
                        text = "Subtask description",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8A2BE2),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF8A2BE2)
                ),
                shape = RoundedCornerShape(8.dp),
                maxLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    ),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = { onAddSubTask(tempTitle, tempDescription) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8A2BE2)
                    ),
                    enabled = tempTitle.isNotBlank()
                ) {
                    Text("Add Subtask")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
                }
            ) {
                Text(
                    text = "OK",
                    color = Color(0xFF8A2BE2)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = Color.Gray
                )
            }
        },
        text = {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = NiyamColors.backgroundColor,
                    titleContentColor = Color.White,
                    headlineContentColor = Color.White,
                    weekdayContentColor = Color.Gray,
                    subheadContentColor = Color.Gray,
                    yearContentColor = Color.White,
                    currentYearContentColor = Color(0xFF8A2BE2),
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = Color(0xFF8A2BE2),
                    dayContentColor = Color.White,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = Color(0xFF8A2BE2),
                    todayContentColor = Color(0xFF8A2BE2),
                    todayDateBorderColor = Color(0xFF8A2BE2)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = NiyamColors.surfaceBackgroundColor,
        modifier = Modifier.fillMaxWidth(),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}