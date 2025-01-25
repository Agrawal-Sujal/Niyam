package com.project.niyam.presentation.screens.view.tasks

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.presentation.screens.view.preview.TaskPreview
import com.project.niyam.presentation.screens.viewmodels.tasks.TasksHomeScreenViewModel
import com.project.niyam.utils.DateDetail
import com.project.niyam.utils.DateTimeDetail
import com.project.niyam.utils.daysRemaining
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TasksHomeScreen(
    onCreateStrictTask: (String) -> Unit,
    context: Context,
    homeViewModel: TasksHomeScreenViewModel = hiltViewModel(),
    onCreateTask: (String) -> Unit
) {
    val uiState by homeViewModel.uiState

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
        ) {
            TodayDate()
            HorizontalCalendar(homeViewModel, uiState.date)
            Spacer(modifier = Modifier.height(8.dp))
            RoutinePartUI(
                viewModel = homeViewModel,
                uiState.date,
                context = context,
            )
            FloatingActionButton(
                onClick = { onCreateTask(uiState.date) },
                modifier = Modifier
                    .padding(20.dp),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }


        }
        FloatingActionButton(
            onClick = { onCreateStrictTask(uiState.date) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary,
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodayDate() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${DateTimeDetail.FULL_MONTH_NAME.getDetail()} ${DateTimeDetail.DATE.getDetail()}, ${
                DateTimeDetail.FULL_YEAR.getDetail()
            }",
            modifier = Modifier.padding(top = 12.dp, start = 12.dp),
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HorizontalCalendar(
    viewModel: TasksHomeScreenViewModel,
    selectedDate: String,
) {
    val datesList: List<LocalDate> =
        viewModel.giveDateLists()

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clickable {
                    viewModel.decreaseDate()
                },
        )
        datesList.forEach {
            val fullDate = DateDetail.FULL_DATE.getDetail(it)
            val date = DateDetail.DATE.getDetail(it)
            val day = DateDetail.SHORT_DAY_NAME.getDetail(it)

            Column(modifier = Modifier.clickable { viewModel.changeDate(fullDate) }) {
                Text(
                    text = day,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp),
                    color = if (fullDate == selectedDate) {
                        Color.Blue
                    } else {
                        Color.Black
                    },
                )
                Text(
                    text = date,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                        .align(Alignment.CenterHorizontally),
                    color = if (fullDate == selectedDate) {
                        Color.Blue
                    } else {
                        Color.Black
                    },
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clickable {
                    viewModel.increaseDate()
                },
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoutinePartUI(
    viewModel: TasksHomeScreenViewModel,
    date: String,
    context: Context,
) {
    val routineList = remember(key1 = date) {
        viewModel.getAllStrictTask(date)
    }.collectAsState().value
    val task = remember(key1 = date) { viewModel.getAllTask(date) }.collectAsState().value
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        item {
            routineList.forEach { item ->
                StrictTaskUI(
                    id = item.id,
                    task = item.taskName,
                    description = item.taskDescription,
                    time = item.startTime,
                    context = context,
                    endTime = item.endTime,
                    isStrict = true
                )
            }
            Text("Weekly Tasks")
            task.forEach { item ->
                val daysRemaining: String = daysRemaining(item.endDate).toString()
                StrictTaskUI(
                    id = item.id,
                    task = item.taskName,
                    description = item.taskDescription,
                    time = daysRemaining,
                    context = context,
                    endTime = item.minutesRemaining,
                    isStrict = false
                )
            }
        }
//        items(items = routineList, key = { it.id }) { item ->
//
//        }
    }
}


@Composable
fun StrictTaskUI(
    id: Int,
    task: String,
    description: String,
    time: String,
    context: Context,
    endTime: String,
    isStrict: Boolean
) {
    Row(
        modifier = Modifier
            .padding(
                8.dp,
            )
    ) {
        TaskUI(
            task = task,
            description = description,
            time = time,
            context = context,
            id = id,
            endTime = endTime,
            isStrict = isStrict
        )
    }
}

@Composable
fun TaskUI(
    task: String = "",
    description: String = "",
    time: String,
    context: Context,
    id: Int,
    endTime: String,
    isStrict: Boolean
) {
    Card(
        modifier = Modifier
            .clickable {
                val intent = Intent(context, TaskPreview::class.java)
                intent.action = "subTask"
                intent.putExtra("id", id.toString())
                if (isStrict) intent.putExtra("Strict", "true")
                else intent.putExtra("Strict", "false")
                context.startActivity(intent)
            }
            .fillMaxWidth(),
    ) {
        Box {
            Text(
                text = time,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .padding(end = 12.dp),
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier
                    .size(25.dp)
                    .clickable {
                    }
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = task,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = description, modifier = Modifier.padding(start = 16.dp, top = 3.dp))
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

