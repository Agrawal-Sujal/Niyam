package com.project.niyam.presentation.screens.view.tasks

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.niyam.R
import com.project.niyam.presentation.screens.viewmodels.tasks.CreateStrictTaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateStrictTask(
    viewModel: CreateStrictTaskViewModel,
    date: String,
    onClick: () -> Unit,
    navigateToCreateSubTaskScreen: (String, String) -> Unit,
    id: String
) {
    val uiState by viewModel.uiState
    viewModel.updateDate(date)
    val context = LocalContext.current
    if (id != "-1") {
        LaunchedEffect(key1 = id) {
            viewModel.loadStrictTask(id.toInt())
        }
    }
    LazyColumn(modifier = Modifier.padding(12.dp)) {
        item {
            TextField(
                label = "Task Name",
                onValueChange = { viewModel.updateName(it) },
                value = uiState.name,
            )
            TextField(
                label = "Task Description",
                onValueChange = { viewModel.updateDescription(it) },
                value = uiState.description,
            )
            Text("Start Time", color = colorResource(R.color.PrimaryColorText))
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                TextField(
                    label = "Hour",
                    onValueChange = { viewModel.updateStartH(it) },
                    value = uiState.startH,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 12.dp, top = 0.dp)
                        .weight(1f),
                    isError = if (uiState.startH != "") uiState.startH.toInt() > 24 else false
                )
                Text(
                    ":",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically),
                    fontSize = 30.sp
                )
                TextField(
                    label = "Minute",
                    onValueChange = { viewModel.updateStartMin(it) },
                    value = uiState.startMin,
                    keyboardType = KeyboardType.Number,
                    isError = if (uiState.startMin != "") uiState.startMin.toInt() > 60 else false,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 12.dp, top = 0.dp)
                        .weight(1f),
                )
            }

            Text("End Time", color = colorResource(R.color.PrimaryColorText))
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                TextField(
                    label = "Hour",
                    onValueChange = { viewModel.updateEndH(it) },
                    value = uiState.endH,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 12.dp, top = 0.dp)
                        .weight(1f),
                    isError = if (uiState.endH != "") uiState.endH.toInt() > 24 else false
                )
                Text(
                    ":",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically),
                    fontSize = 30.sp
                )
                TextField(
                    label = "Minute",
                    onValueChange = { viewModel.updateEndMin(it) },
                    value = uiState.endMIn,
                    keyboardType = KeyboardType.Number,
                    isError = if (uiState.endMIn != "") uiState.endMIn.toInt() > 60 else false,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 12.dp, top = 0.dp)
                        .weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Sub Tasks",
                    color = colorResource(R.color.PrimaryColorText),
                    fontSize = 20.sp
                )
                FloatingActionButton(
                    onClick = {
                        navigateToCreateSubTaskScreen("-1", "-1")
                    }, containerColor = colorResource(R.color.PrimaryColor),
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            uiState.subTasks.forEachIndexed { it1, it ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SubTask(
                        name = it.subTaskName,
                        description = it.subTaskDescription,
                        onDelete = { viewModel.removeSubTask(it) },
                        onEdit = { navigateToCreateSubTaskScreen(it1.toString(), id) })
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier
                        .padding(12.dp),
                    onClick = {
                        if (viewModel.checkTask()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                if (id == "-1")
                                    viewModel.saveTask(date = date)
                                else viewModel.updateTask(date = date, id)
                            }
                            onClick()
                        } else {
                            Toast.makeText(context, "Fill All Information", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    colors = ButtonColors(
                        containerColor = colorResource(R.color.PrimaryColorText),
                        contentColor = colorResource(R.color.NormalText),
                        disabledContainerColor = colorResource(R.color.PrimaryColorText),
                        disabledContentColor = colorResource(R.color.PrimaryColorText)
                    )
                ) {
                    if (id == "-1")
                        Text("Save", color = Color.Black, fontSize = 20.sp)
                    else Text("Update", color = Color.Black, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
private fun SubTask(name: String, description: String, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 8.dp), colors = CardColors(
            contentColor = colorResource(R.color.NormalText),
            containerColor = colorResource(R.color.PrimaryColor),
            disabledContainerColor = colorResource(R.color.PrimaryColor),
            disabledContentColor = colorResource(R.color.NormalText)
        )
    ) {
        Column {
            Text(name, fontSize = 32.sp, modifier = Modifier.padding(12.dp))
            Text(description, fontSize = 16.sp, modifier = Modifier.padding(12.dp))
            Row {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Icon",
                    modifier = Modifier.clickable {
                        onDelete()
                    })
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Icon",
                    modifier = Modifier.clickable {
                        onEdit()
                    })
            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType? = null,
    last: Boolean = false,
    modifier: Modifier? = null,
    isError: Boolean = false

) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        modifier = modifier ?: Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 12.dp, top = 0.dp),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = colorResource(R.color.PrimaryColorText),
            unfocusedBorderColor = colorResource(R.color.NormalText),
            cursorColor = colorResource(R.color.PrimaryColorText),
            focusedTextColor = colorResource(R.color.NormalText),
            errorBorderColor = Color.Red
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType ?: KeyboardType.Text,
            imeAction = if (last) ImeAction.Done else ImeAction.Next
        ),
        isError = isError
    )


}
