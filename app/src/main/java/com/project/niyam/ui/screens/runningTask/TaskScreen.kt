package com.project.niyam.ui.screens.runningTask

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.utils.TimerState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskScreen(
    viewModel: TaskScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState { state.subTasks.size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Remaining Time
        Text(
            text = state.remainingTime,
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(Modifier.height(16.dp))

        // Subtasks Pager
        if (state.subTasks.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                val subTask = state.subTasks[page]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(subTask.name, style = MaterialTheme.typography.titleLarge)
                        subTask.description?.let {
                            Text(it, style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.markSubTaskDone(subTask.id) },
                            enabled = !subTask.isCompleted,
                        ) {
                            Text(if (subTask.isCompleted) "Completed" else "Done")
                        }
                    }
                }
            }

            // Pager indicator
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                repeat(state.subTasks.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (selected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                },
                            ),
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Flexible Task Controls
        if (state.isFlexible) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                when (state.timerState) {
                    TimerState.IDLE -> {
                        Button(onClick = { viewModel.start() }) {
                            Text("Start")
                        }
                    }
                    TimerState.RUNNING -> {
                        Button(onClick = { viewModel.pause() }) {
                            Text("Pause")
                        }
                        Button(onClick = { viewModel.done() }) {
                            Text("Done")
                        }
                    }
                    TimerState.PAUSED -> {
                        Button(onClick = { viewModel.resume() }) {
                            Text("Resume")
                        }
                        Button(onClick = { viewModel.done() }) {
                            Text("Done")
                        }
                    }
                    TimerState.DONE -> {
                        Text("Task Completed", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
