package com.project.niyam.presentation.screens.view.tasks

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.presentation.screens.viewmodels.tasks.CreateSubTaskUiState
import com.project.niyam.presentation.screens.viewmodels.tasks.CreateTaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateStrictTask(
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
                Text("Enter Start Time (HH:MM) :")
                TextField(value = uiState.startTime, onValueChange = {
                    viewModel.updateStartDate(it)
                }, modifier = Modifier.padding(start = 12.dp))
            }
            Row(modifier = Modifier.padding(12.dp)) {
                Text("Enter End Time (HH:MM) :")
                TextField(value = uiState.endTime, onValueChange = {
                    viewModel.updateEndDate(it)
                }, modifier = Modifier.padding(start = 12.dp))
            }



            Spacer(modifier = Modifier.height(24.dp))

//            val subTaskList1 = remember(key1 = date) { viewModel.getSubTasks() }
//            val subTaskList2 = subTaskList1.collectAsState()
//            val subTaskList = subTaskList2.value

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
                }
            ) {
                Text("Save")
            }
        }
    }
}

