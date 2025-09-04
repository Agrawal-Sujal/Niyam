package com.project.niyam.ui.screens.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.R
import com.project.niyam.ui.theme.NiyamColors
import com.project.niyam.utils.TimerState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onUpdateTask: (taskId: Int, isTimeBound: Boolean) -> Unit,
    onTimeBoundClicked: (taskId: Int) -> Unit,
    onFlexibleClicked: (taskId: Int) -> Unit,
) {
    val state by viewModel.ui.collectAsState()
    val today = LocalDate.now()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val days = remember {
        (-30..30).map { today.plusDays(it.toLong()) }
    }

    val todayIndex = remember { days.indexOf(today) }

    // Auto-scroll to today on first launch
    LaunchedEffect(Unit) {
        if (todayIndex != -1) {
            listState.scrollToItem(todayIndex)
        }
    }

    Column(
        Modifier
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        // Header date row (e.g., "August 21, 2025")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = state.selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                color = NiyamColors.whiteColor
            )
//            Button(onClick = { viewModel.syncTasks() }) {
//                Text("Sync Tasks")
//            }
//            Button(onClick = { viewModel.logout() }) {
//                Text("Log Out")
//            }

            if (state.selectedDate != today) {
                AssistChip(
                    onClick = {
                        coroutineScope.launch {
                            if (todayIndex != -1) {
                                val centerOffset = listState.layoutInfo.viewportEndOffset / 2
                                listState.animateScrollToItem(
                                    todayIndex,
                                    scrollOffset = -centerOffset,
                                )
                                viewModel.selectDate(today)
                            }
                        }
                    },
                    label = {
                        Text(
                            "Today",
                            style = MaterialTheme.typography.bodySmall, // smaller text
                            color = NiyamColors.whiteColor,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Today,
                            contentDescription = "Today",
                            modifier = Modifier.size(16.dp), // smaller icon
                            tint = NiyamColors.blueColor
                        )
                    },
                    modifier = Modifier.height(28.dp), // reduce chip height
                    shape = MaterialTheme.shapes.small, // less rounded
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Week strip with arrows
        DatePickerRow(
            selectedDate = state.selectedDate,
            onDateSelected = { viewModel.selectDate(it) },
            listState,
            days,
        )

        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(),color = NiyamColors.blueColor, trackColor = NiyamColors.blueColor)
        }

        state.errorMessage?.let { msg ->
            AssistChip(
                onClick = {},
                label = { Text(msg) },
                modifier = Modifier.padding(16.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    labelColor = MaterialTheme.colorScheme.onErrorContainer,
                ),
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
        ) {
            // Time Bound Section
            if (state.timeBoundTasks.isNotEmpty()) {
//                item {
//                    SectionHeader("Time-bound Tasks")
//                }
                items(state.timeBoundTasks) { t ->
                    val status = giveStatus(t.completed,t.status)
                    TaskCard(
                        totalTime = t.totalTimeAllocated,
                        title = t.taskName,
                        progress = "",
                        dueDate = t.endTime.toString(),
                        timeRemaining = t.timeRemaining.toString(),
                        status = status.status,
                        iconRes = R.drawable.img,
                        backgroundColor = status.bgColor,
                        statusColor = status.statusColor,
                        onClick = {
                            onTimeBoundClicked(t.id)
                        }
                    )
                }
            }

            // Flexible Section
            if (state.flexibleTasks.isNotEmpty()) {
//                item {
//                    SectionHeader("Weekly Tasks")
//                }
                items(state.flexibleTasks) { t ->
                    val status = giveStatus(t.completed,t.state)
                    TaskCard(
                        totalTime = (t.hoursAlloted*60*60).toLong(),
                        title = t.taskName,
                        progress = "",
                        dueDate = t.windowEndDate.toString(),
                        timeRemaining = t.timeRemaining.toString(),
                        status = status.status,
                        iconRes = R.drawable.img,
                        backgroundColor = status.bgColor,
                        statusColor = status.statusColor,
                        onClick = {
                            onTimeBoundClicked(t.id)
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerRow(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    listState: LazyListState,
    days: List<LocalDate>,
) {
    Column {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            itemsIndexed(days) { index, date ->
                val isSelected = date == selectedDate
                DateChip(
                    date = date,
                    isSelected = isSelected,
                    onClick = {
                        onDateSelected(date)
                    },
                )
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Header(today: LocalDate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Today",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = today.format(DateTimeFormatter.ofPattern("d MMM")),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateChip(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor =
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.background
        }
    val contentColor =
        if (isSelected) {
            NiyamColors.whiteColor
        } else {
            NiyamColors.greyColor
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
            ) { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
    ) {
        Text(
            text = date.dayOfWeek.name.take(3), // MON, TUE
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
        )
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = contentColor,
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@Composable
fun TaskCard(
    totalTime :Long,
    title: String,
    progress: String,
    dueDate: String,
    timeRemaining: String,
    status: String,
    iconRes: Int,
    backgroundColor: Color,
    statusColor: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
            ) {onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Status Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(status, color = Color.White, style = MaterialTheme.typography.labelSmall)
                }

                // Due Date
                Text(
                    text = "Due Date : $dueDate",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Icon + Title + Progress
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Text(progress, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                }


                TimerWithProgress(totalTimeSeconds = totalTime.toInt(), timeRemainingSeconds = timeRemaining.toInt(),color =statusColor)
            }
        }
    }
}


@Composable
fun TimerWithProgress(
    totalTimeSeconds: Int,
    timeRemainingSeconds: Int,
    color: Color
) {
    // Calculate progress (completed portion)
    val progress = if (totalTimeSeconds > 0) {
        ((totalTimeSeconds - timeRemainingSeconds).toFloat() / totalTimeSeconds.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    // Format time remaining in smallest possible manner
    val formattedTime = formatTimeRemaining(timeRemainingSeconds)

    Box(
        modifier = Modifier.size(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Draw background circle (white)
            drawArc(
                color = Color.White,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx())
            )

            // Draw progress arc (blue for completed portion)
            if (progress > 0f) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }
        Text(
            text = formattedTime,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

fun formatTimeRemaining(seconds: Int): String {
    return when {
        seconds >= 3600 -> { // 1 hour or more
            val hours = seconds / 3600.0
            "${String.format("%.1f", hours)} hr"
        }
        seconds >= 60 -> { // 1 minute or more
            val minutes = seconds / 60
            "${minutes} min"
        }
        else -> { // Less than 1 minute
            "${seconds} sec"
        }
    }
}


data class UiStatus(val status: String, val bgColor: Color,val statusColor : Color)
fun giveStatus(isCompleted: Boolean,state : TimerState): UiStatus{
    if(isCompleted ){
        return UiStatus(status = "Completed", statusColor = Color(0xFFFF7426), bgColor = Color(0xFF304431))
    }
    return when(state){
        TimerState.IDLE -> UiStatus(status = "Pending", statusColor = Color(0xFFF43535), bgColor = Color(0xFF443030))

        TimerState.RUNNING -> UiStatus(status = "In Progress", statusColor = Color(0xFF5869FF), bgColor = Color(0xFF303044))

        TimerState.PAUSED -> UiStatus(status = "Paused", statusColor = Color(0xFFF2B720), bgColor = Color(0xFF443041))

        TimerState.DONE -> UiStatus(status = "Not Completed", statusColor = Color(0xFFF43535), bgColor = Color(0xFF443030))
    }
}
