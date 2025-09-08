package com.project.niyam.ui.screens.addTimeBoundTask

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.ui.theme.NiyamColors
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTimeBoundTaskScreen(
    viewModel: AddTimeBoundedTaskViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    // Time picker states
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NiyamColors.backgroundColor)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // Header
            Text(
                text = "Create New Task",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NiyamColors.whiteColor,
                modifier = Modifier.padding(bottom = 32.dp),
            )

            // Title Field
            Text(
                text = "Title",
                color = NiyamColors.whiteColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                placeholder = {
                    Text(
                        text = "Enter task title",
                        color = NiyamColors.greyColor,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NiyamColors.blueColor,
                    unfocusedBorderColor = NiyamColors.greyColor,
                    focusedTextColor = NiyamColors.whiteColor,
                    unfocusedTextColor = NiyamColors.whiteColor,
                    cursorColor = NiyamColors.blueColor,
                ),
                shape = RoundedCornerShape(8.dp),
            )

            // Description Field
            Text(
                text = "Description",
                color = NiyamColors.whiteColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChanged,
                placeholder = {
                    Text(
                        text = "Enter task description",
                        color = NiyamColors.greyColor,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NiyamColors.blueColor,
                    unfocusedBorderColor = NiyamColors.greyColor,
                    focusedTextColor = NiyamColors.whiteColor,
                    unfocusedTextColor = NiyamColors.whiteColor,
                    cursorColor = NiyamColors.blueColor,
                ),
                shape = RoundedCornerShape(8.dp),
                maxLines = 4,
            )

            // Start Time
            Text(
                text = "Start Time",
                color = NiyamColors.whiteColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            OutlinedTextField(
                value = uiState.startTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
                onValueChange = { },
                placeholder = {
                    Text(
                        text = "Select start time",
                        color = NiyamColors.greyColor,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NiyamColors.blueColor,
                    unfocusedBorderColor = NiyamColors.greyColor,
                    focusedTextColor = NiyamColors.whiteColor,
                    unfocusedTextColor = NiyamColors.whiteColor,
                    cursorColor = NiyamColors.blueColor,
                ),
                shape = RoundedCornerShape(8.dp),

                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Select time",
                        tint = NiyamColors.greyColor,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current,
                        ) { showStartTimePicker = true },
                    )
                },
            )

            // End Time
            Text(
                text = "End Time",
                color = NiyamColors.whiteColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            OutlinedTextField(
                value = uiState.endTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
                onValueChange = { },
                placeholder = {
                    Text(
                        text = "Select end time",
                        color = NiyamColors.greyColor,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NiyamColors.blueColor,
                    unfocusedBorderColor = NiyamColors.greyColor,
                    focusedTextColor = NiyamColors.whiteColor,
                    unfocusedTextColor = NiyamColors.whiteColor,
                    cursorColor = NiyamColors.blueColor,
                ),
                shape = RoundedCornerShape(8.dp),

                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Select time",
                        tint = NiyamColors.greyColor,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current,
                        ) { showEndTimePicker = true },
                    )
                },
            )

            // Subtasks Section
            Text(
                text = "Subtasks",
                color = NiyamColors.whiteColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            // Display existing subtasks
            uiState.subTasks.forEachIndexed { idx, subtask ->
                SubTaskItem(
                    subtask = subtask,
                    onRemove = { viewModel.removeSubTask(idx) },
                )
            }

            // Add Subtask Button
            OutlinedButton(
                onClick = { viewModel.openSubTaskSheet() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NiyamColors.blueColor,
                ),
                border = BorderStroke(1.dp, NiyamColors.blueColor),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add subtask",
                    modifier = Modifier.size(20.dp),
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
                    containerColor = NiyamColors.blueColor,
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Create Task",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }

    // Time Pickers
    if (showStartTimePicker) {
        TimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onTimeSelected = { time ->
                viewModel.onStartTimeSelected(time)
                showStartTimePicker = false
            },
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismiss = { showEndTimePicker = false },
            onTimeSelected = { time ->
                viewModel.onEndTimeSelected(time)
                showEndTimePicker = false
            },
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
            onDismiss = { viewModel.dismissSubTaskSheet() },
        )
    }

    // Cancel Dialog
    if (uiState.showCancelDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissCancelDialog() },
            title = {
                Text(
                    text = "Cancel Task Creation",
                    color = NiyamColors.whiteColor,
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to cancel? All unsaved changes will be lost.",
                    color = NiyamColors.greyColor,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.dismissCancelDialog()
                        onNavigateBack()
                    },
                ) {
                    Text(
                        text = "Yes, Cancel",
                        color = NiyamColors.blueColor,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissCancelDialog() },
                ) {
                    Text(
                        text = "Continue Editing",
                        color = NiyamColors.greyColor,
                    )
                }
            },
            containerColor = Color(0xFF2A2A2A),
        )
    }
}

@Composable
fun SubTaskItem(
    subtask: SubTaskUi,
    onRemove: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = NiyamColors.surfaceBackgroundColor,
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = subtask.title,
                    color = NiyamColors.whiteColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                )
                if (subtask.description.isNotBlank()) {
                    Text(
                        text = subtask.description,
                        color = NiyamColors.greyColor,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove subtask",
                    tint = Color(0xFFFF4444),
                    modifier = Modifier.size(20.dp),
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
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = NiyamColors.surfaceBackgroundColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Text(
                text = "Add Subtask",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NiyamColors.whiteColor,
                modifier = Modifier.padding(bottom = 24.dp),
            )

            OutlinedTextField(
                value = tempTitle,
                onValueChange = onTitleChange,
                placeholder = {
                    Text(
                        text = "Subtask title",
                        color = NiyamColors.greyColor,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NiyamColors.blueColor,
                    unfocusedBorderColor = NiyamColors.greyColor,
                    focusedTextColor = NiyamColors.whiteColor,
                    unfocusedTextColor = NiyamColors.whiteColor,
                    cursorColor = NiyamColors.blueColor,
                ),
                shape = RoundedCornerShape(8.dp),
            )

            OutlinedTextField(
                value = tempDescription,
                onValueChange = onDescriptionChange,
                placeholder = {
                    Text(
                        text = "Subtask description",
                        color = Color.Gray,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NiyamColors.blueColor,
                    unfocusedBorderColor = NiyamColors.greyColor,
                    focusedTextColor = NiyamColors.whiteColor,
                    unfocusedTextColor = NiyamColors.whiteColor,
                    cursorColor = NiyamColors.blueColor,
                ),
                shape = RoundedCornerShape(8.dp),
                maxLines = 3,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray,
                    ),
                    border = BorderStroke(1.dp, Color.Gray),
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = { onAddSubTask(tempTitle, tempDescription) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8A2BE2),
                    ),
                    enabled = tempTitle.isNotBlank(),
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
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
) {
    val timePickerState = rememberTimePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedTime = LocalTime.of(
                        timePickerState.hour,
                        timePickerState.minute,
                    )
                    onTimeSelected(selectedTime)
                },
            ) {
                Text(
                    text = "OK",
                    color = Color(0xFF8A2BE2),
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = Color.Gray,
                )
            }
        },
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = Color(0xFF2A2A2A),
                    selectorColor = Color(0xFF8A2BE2),
                    containerColor = Color(0xFF1A1A1A),
                    periodSelectorBorderColor = Color(0xFF8A2BE2),
                    clockDialSelectedContentColor = Color.White,
                    clockDialUnselectedContentColor = Color.Gray,
                    periodSelectorSelectedContainerColor = Color(0xFF8A2BE2),
                    periodSelectorUnselectedContainerColor = Color.Transparent,
                    periodSelectorSelectedContentColor = Color.White,
                    periodSelectorUnselectedContentColor = Color.Gray,
                    timeSelectorSelectedContainerColor = Color(0xFF8A2BE2),
                    timeSelectorUnselectedContainerColor = Color(0xFF2A2A2A),
                    timeSelectorSelectedContentColor = Color.White,
                    timeSelectorUnselectedContentColor = Color.Gray,
                ),
            )
        },
        containerColor = Color(0xFF2A2A2A),
    )
}
