package com.project.niyam.presentation.screens.view.tasks

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.R
import com.project.niyam.domain.model.SubTasks
import com.project.niyam.presentation.screens.view.preview.StrictTaskPreview
import com.project.niyam.presentation.screens.view.preview.TaskPreview
import com.project.niyam.presentation.screens.viewmodels.tasks.TasksHomeScreenViewModel
import com.project.niyam.utils.DateDetail
import com.project.niyam.utils.DateTimeDetail
import com.project.niyam.utils.daysRemaining
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TasksHomeScreen(
    onCreateStrictTask: (String, String) -> Unit,
    context: Context,
    homeViewModel: TasksHomeScreenViewModel = hiltViewModel(),
    onCreateTask: (String, String) -> Unit,
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
                onTaskSelected = {
                    homeViewModel.setRunningTask(it)
                },
                onCreateStrictTask,
                onCreateTask,
            )
        }
        if (DateTimeDetail.FULL_DATE.getDetail() <= uiState.date) {
            ExpandableFABExample(onCreateTask = {
                onCreateTask(uiState.date, "-1")
            }, onCreateStrictTask = {
                onCreateStrictTask(uiState.date, "-1")
            })
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
                        colorResource(R.color.PrimaryColorText)
                    } else {
                        colorResource(R.color.NormalText)
                    },
                    fontSize = if (fullDate == selectedDate) {
                        18.sp
                    } else {
                        14.sp
                    },
                )
                Text(
                    text = date,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                        .align(Alignment.CenterHorizontally),
                    color = if (fullDate == selectedDate) {
                        colorResource(R.color.PrimaryColorText)
                    } else {
                        colorResource(R.color.NormalText)
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
    onTaskSelected: (Int) -> Unit,
    onCreateStrictTask: (String, String) -> Unit,
    onCreateTask: (String, String) -> Unit,
) {
    val task = remember(key1 = date) { viewModel.getAllTask(date) }.collectAsState().value

    val routineList = remember(key1 = date) {
        viewModel.getAllStrictTask(date)
    }.collectAsState().value
    Log.d("Testing", "Normal Tasks$task on date : $date")
    val strictTaskRunningId = remember(key1 = date) {
        viewModel.getStrictTaskRunning()
    }.collectAsState().value
    val normalTaskRunningId = remember(key1 = date) {
        viewModel.getNormalTaskRunning()
    }.collectAsState().value
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        item {
            routineList.forEach { item ->
//                val status: Int = if (item.isCompleted == 0) {
//                    if (allSubTaskCompleted(item.subTasks)) {
//                        1
//                    } else {
//                        -1
//                    }
//                } else {
//                    if (strictTaskRunningId == item.id) {
//                        2
//                    } else if (isCurrentTimeInRange(item.startTime, item.endTime)) {
//                        0
//                    } else if (isCurrentTimeGreater(item.endTime)) {
//                        -1
//                    } else {
//                        0
//                    }
//                }
                val status: Int = if (strictTaskRunningId == item.id) {
                    2
                } else if (isCurrentTimeGreater(item.endTime)) {
                    -1
                } else {
                    item.isCompleted
                }
                StrictTaskUI(
                    id = item.id,
                    task = item.taskName,
                    description = item.taskDescription,
                    time = item.startTime,
                    context = context,
                    endTime = item.endTime,
                    isStrict = true,
                    onTaskSelected,
                    viewModel,
                    onDelete = {
                        viewModel.removeStrictTask(item)
                    },
                    onEdit = {
                        onCreateStrictTask(date, item.id.toString())
                    },
                    enable = isToday(date) && isCurrentTimeInRange(
                        item.startTime,
                        item.endTime,
                    ),
                    status = status,
                    strictTaskRunningId,
                    normalTaskRunningId,
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
            )
            task.forEach { item ->
//                val status: Int = if (item.isCompleted) {
//                    if (allSubTaskCompleted(item.subTasks)) {
//                        1
//                    } else {
//                        -1
//                    }
//                } else {
//                    if (normalTaskRunningId == item.id) {
//                        2
//                    } else if (isTodayOrAfter(item.startDate) && isTodayOrBefore(item.endDate)) {
//                        0
//                    } else {
//                        -1
//                    }
//                }
                val status: Int = if (normalTaskRunningId == item.id) {
                    2
                } else {
                    item.isCompleted
                }
                val daysRemaining: String = daysRemaining(item.endDate).toString()
                if (daysRemaining == "1") {
                    StrictTaskUI(
                        id = item.id,
                        task = item.taskName,
                        description = item.taskDescription,
                        time = daysRemaining,
                        context = context,
                        endTime = item.secondsRemaining,
                        isStrict = false,
                        onTaskSelected,
                        viewModel,
                        onDelete = { viewModel.removeTask(item) },
                        onEdit = { onCreateTask(date, item.id.toString()) },
                        enable = isToday(date = date),
                        status = status,
                        strictTaskRunningId,
                        normalTaskRunningId,
                    )
                }
            }

            Text("Weekly Tasks", color = colorResource(R.color.PrimaryColorText))
            task.forEach { item ->
//                val status: Int = if (item.isCompleted) {
//                    if (allSubTaskCompleted(item.subTasks)) {
//                        1
//                    } else {
//                        -1
//                    }
//                } else {
//                    if (normalTaskRunningId == item.id) {
//                        2
//                    } else if (isTodayOrAfter(item.startDate) && isTodayOrBefore(item.endDate)) {
//                        0
//                    } else {
//                        -1
//                    }
//                }
                val status: Int = if (normalTaskRunningId == item.id) {
                    2
                } else {
                    item.isCompleted
                }
                val daysRemaining: String = daysRemaining(item.endDate).toString()
                if (daysRemaining != "1") {
                    StrictTaskUI(
                        id = item.id,
                        task = item.taskName,
                        description = item.taskDescription,
                        time = daysRemaining,
                        context = context,
                        endTime = item.secondsRemaining,
                        isStrict = false,
                        onTaskSelected,
                        viewModel,
                        onDelete = { viewModel.removeTask(item) },
                        onEdit = { onCreateTask(date, item.id.toString()) },
                        enable = isToday(date = date),
                        status = status,
                        strictTaskRunningId,
                        normalTaskRunningId,
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StrictTaskUI(
    id: Int,
    task: String,
    description: String,
    time: String,
    context: Context,
    endTime: String,
    isStrict: Boolean,
    onTaskSelected: (Int) -> Unit,
    viewModel: TasksHomeScreenViewModel,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    enable: Boolean,
    status: Int,
    strictTaskRunning: Int,
    normalTaskRunning: Int,
) {
    Row(
        modifier = Modifier
            .padding(
                8.dp,
            ),
    ) {
        TaskUI(
            task = task,
            description = description,
            time = time,
            context = context,
            id = id,
            endTime = endTime,
            isStrict = isStrict,
            onTaskSelected,
            viewModel,
            onDelete,
            onEdit,
            enable = enable,
            status = status,
            strictTaskRunning,
            normalTaskRunning,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskUI(
    task: String = "",
    description: String = "",
    time: String,
    context: Context,
    id: Int,
    endTime: String,
    isStrict: Boolean,
    onTaskSelected: (Int) -> Unit,
    viewModel: TasksHomeScreenViewModel,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    enable: Boolean,
    status: Int,
    strictTaskRunning: Int,
    normalTaskRunning: Int,
) {
    Card(
        modifier = Modifier
            .clickable {
                if (enable && (status == 0 || status == 2)) {
                    Log.d(
                        "Testing",
                        "Strict Task Running Id : $strictTaskRunning , Normal Task Running Id: $normalTaskRunning , selected Item Id : $id",
                    )
                    if (isStrict && normalTaskRunning == 0 && (strictTaskRunning == 0 || strictTaskRunning == id)) {
                        onTaskSelected(id)
                        val intent = Intent(context, StrictTaskPreview::class.java)
                        intent.action = "subTask"
                        intent.putExtra("id", id.toString())
                        intent.putExtra("Strict", "true")
                        context.startActivity(intent)
                    } else if (!isStrict && strictTaskRunning == 0 && (normalTaskRunning == 0 || normalTaskRunning == id)) {
                        val intent = Intent(context, TaskPreview::class.java)
                        intent.action = "subTask"
                        intent.putExtra("id", id.toString())
                        intent.putExtra("Strict", "false")
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "Task is already running",
                            Toast.LENGTH_SHORT,
                        )
                            .show()
                    }
                }
            }
            .fillMaxWidth(),
        colors = CardColors(
            containerColor = colorResource(R.color.PrimaryColor),
            contentColor = Color(0xffE7E7E7),
            disabledContainerColor = Color(0xffEDFFFF),
            disabledContentColor = Color(0xffEDFFFF),
        ),

    ) {
        Box {
            Text(
                text = time,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .padding(end = 12.dp),
            )
            Row(modifier = Modifier.align(Alignment.BottomEnd)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            Log.d(
                                "Testing",
                                "Strict Task Running Id : $strictTaskRunning , Normal Task Running Id: $normalTaskRunning , selected Item Id : $id",
                            )
                            if (isStrict && (strictTaskRunning == 0 || strictTaskRunning != id)) {
                                onDelete()
                            } else if (!isStrict && (normalTaskRunning == 0 || normalTaskRunning != id)) {
                                onDelete()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Finish the Task or stop the task",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                        .padding(4.dp),
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Icon",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            Log.d(
                                "Testing",
                                "Strict Task Running Id : $strictTaskRunning , Normal Task Running Id: $normalTaskRunning , selected Item Id : $id",
                            )
                            if (isStrict && (strictTaskRunning == 0 || strictTaskRunning != id)) {
                                onEdit()
                            } else if (!isStrict && (normalTaskRunning == 0 || normalTaskRunning != id)) {
                                onEdit()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Finish the Task or stop the task",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                        .padding(4.dp),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Row {
                    if (status == -1) Text("Not Completed")
                    if (status == 0) Text("Pending")
                    if (status == 1) Text("Completed")
                    if (status == 2) Text("In Progress")
                }
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

@Composable
fun ExpandableFABExample(onCreateTask: () -> Unit, onCreateStrictTask: () -> Unit) {
    var showFABs by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd,
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (showFABs) {
                Button(
                    onClick = onCreateTask,
                    colors = ButtonColors(
                        containerColor = colorResource(R.color.PrimaryColor),
                        contentColor = colorResource(R.color.PrimaryColor),
                        disabledContainerColor = colorResource(R.color.PrimaryColor),
                        disabledContentColor = colorResource(R.color.PrimaryColor),
                    ),
                ) {
                    Text("Task", color = colorResource(R.color.NormalText))
                }

                Button(
                    onClick = onCreateStrictTask,
                    colors = ButtonColors(
                        containerColor = colorResource(R.color.PrimaryColor),
                        contentColor = colorResource(R.color.PrimaryColor),
                        disabledContainerColor = colorResource(R.color.PrimaryColor),
                        disabledContentColor = colorResource(R.color.PrimaryColor),
                    ),
                ) {
                    Text("Strict Task", color = colorResource(R.color.NormalText))
                }
            }

            FloatingActionButton(
                onClick = { showFABs = !showFABs },
                containerColor = colorResource(R.color.PrimaryColor),
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    }
}

fun isToday(date: String): Boolean {
    return DateTimeDetail.FULL_DATE.getDetail() == date
}

fun isCurrentTimeInRange(startTime: String, endTime: String): Boolean {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentTime = sdf.format(Date())

    return currentTime in startTime..endTime
}

fun isCurrentTimeGreater(endTime: String): Boolean {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentTime = sdf.format(Date())

    return currentTime > endTime
}

fun allSubTaskCompleted(subTask: List<SubTasks>): Boolean {
    subTask.forEach {
        if (!it.isCompleted) return false
    }
    return true
}

fun isTodayOrAfter(date: String): Boolean {
    return DateTimeDetail.FULL_DATE.getDetail() >= date
}

fun isTodayOrBefore(date: String): Boolean {
    return DateTimeDetail.FULL_DATE.getDetail() <= date
}
