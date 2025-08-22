package com.project.niyam.ui.screens.home

import android.R.attr.padding
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.Composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.utils.TaskStatus
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onUpdateTask: (taskId: Int, isTimeBound: Boolean) -> Unit,
    onTimeBoundClicked: (taskId: Int) -> Unit,
    onFlexibleClicked: (taskId: Int) -> Unit
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
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        // Header date row (e.g., "August 21, 2025")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = state.selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f) // pushes Today chip to the end if needed
            )


            if (state.selectedDate != today) {
                AssistChip(
                    onClick = {
                        coroutineScope.launch {
                            if (todayIndex != -1) {
                                val centerOffset = listState.layoutInfo.viewportEndOffset / 2
                                listState.animateScrollToItem(
                                    todayIndex,
                                    scrollOffset = -centerOffset
                                )
                                viewModel.selectDate(today)
                            }
                        }
                    },
                    label = {
                        Text(
                            "Today",
                            style = MaterialTheme.typography.bodySmall // smaller text
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = "Today",
                            modifier = Modifier.size(16.dp) // smaller icon
                        )
                    },
                    modifier = Modifier.height(28.dp), // reduce chip height
                    shape = MaterialTheme.shapes.small // less rounded
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))


        // Week strip with arrows
        DatePickerRow(
            selectedDate = state.selectedDate,
            onDateSelected = { viewModel.selectDate(it) },
            listState,
            days
        )

        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        state.errorMessage?.let { msg ->
            AssistChip(
                onClick = {},
                label = { Text(msg) },
                modifier = Modifier.padding(16.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    labelColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Time Bound Section
            if (state.timeBoundTasks.isNotEmpty()) {
                item {
                    SectionHeader("Time-bound Tasks")
                }
                items(state.timeBoundTasks) { t ->
                    TaskCard(
                        headline = t.taskName,
                        supporting = t.taskDescription ?: "",
//                        leadingTop = statusLabel(t.status),
                        trailingTop = "${t.startTime} - ${t.endTime}",
                        onClick = { onTimeBoundClicked(t.id) },
                        onEdit = { onUpdateTask(t.id, true) },
                        onDelete = { viewModel.deleteTask(t.id, false) }
                    )
                }
            }

            // Flexible Section
            if (state.flexibleTasks.isNotEmpty()) {
                item {
                    SectionHeader("Weekly Tasks")
                }
                items(state.flexibleTasks) { t ->
                    val trailing = when {
                        t.isFirstDay -> "Starts ${t.windowStartTime}"
                        t.isLastDay -> "Ends ${t.windowEndTime}"
                        else -> "${t.daysRemaining}"
                    }
                    TaskCard(
                        headline = t.taskName,
                        supporting = t.taskDescription ?: "",
//                        leadingTop = statusLabel(t.status),
                        trailingTop = trailing,
                        onClick = { onFlexibleClicked(t.id) },
                        onEdit = { onUpdateTask(t.id, false) },
                        onDelete = { viewModel.deleteTask(t.id, true) }
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
    days: List<LocalDate>
) {

    Column {

        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            itemsIndexed(days) { index, date ->
                val isSelected = date == selectedDate
                DateChip(
                    date = date,
                    isSelected = isSelected,
                    onClick = {
                        onDateSelected(date)
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateChip(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current
            ) { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Text(
            text = date.dayOfWeek.name.take(3), // MON, TUE
            style = MaterialTheme.typography.labelMedium,
            color = contentColor
        )
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}


@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

@Composable
private fun TaskCard(
    headline: String,
    supporting: String,
//    leadingTop: String,
    trailingTop: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menu by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current
            ) { onClick() },
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
//                Text(leadingTop, style = MaterialTheme.typography.labelMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(trailingTop, style = MaterialTheme.typography.labelMedium)
                    Box {
                        IconButton(onClick = { menu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = { menu = false; onEdit() },
                                leadingIcon = { Icon(Icons.Default.Edit, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = { menu = false; onDelete() },
                                leadingIcon = { Icon(Icons.Default.Delete, null) }
                            )
                        }
                    }
                }
            }
            Text(headline, style = MaterialTheme.typography.titleMedium)
            if (supporting.isNotBlank()) {
                Text(supporting, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}





