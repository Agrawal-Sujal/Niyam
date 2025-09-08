package com.project.niyam.ui.screens.runningTask

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.ui.theme.NiyamColors
import com.project.niyam.utils.TimerState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(
    viewModel: TaskScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    // Get current subtask (first incomplete or first one if all complete)
    val subTasks = state.subTasks

    // Calculate progress percentage
    val completedTasks = state.subTasks.count { it.isCompleted }
    val progressPercentage = if (state.subTasks.isNotEmpty()) {
        (completedTasks.toFloat() / state.subTasks.size * 100).toInt()
    } else {
        0
    }

// Calculate progress as float between 0.0 and 1.0
    val progressFloat = if (state.subTasks.isNotEmpty()) {
        completedTasks.toFloat() / state.subTasks.size
    } else {
        0f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NiyamColors.backgroundColor), // Dark background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Title Section
            Text(
                text = state.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(top = 32.dp),
            )

            Text(
                text = state.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.7f),
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 40.dp),
            )

            // Timer Display
            Text(
                text = state.remainingTime,
                style = MaterialTheme.typography.displayLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 64.sp,
                    letterSpacing = 2.sp,
                ),
                modifier = Modifier.padding(vertical = 20.dp),
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Progress Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Overall Progress",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.8f),
                    ),
                )
                Text(
                    text = "$progressPercentage%",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            // Progress Bar
            LinearProgressIndicator(
                progress = { progressFloat },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Color.Blue,
                trackColor = Color.Gray,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Current Task Card
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
            ) {
                items(subTasks) { subTask ->
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .height(200.dp), // Fixed height for horizontal scrolling
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (subTask.isCompleted) {
                                Color(0xFF10B981).copy(alpha = 0.15f) // Light green for completed
                            } else {
                                NiyamColors.primaryColor
                            },
                        ),
                        border = if (subTask.isCompleted) {
                            BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                        } else {
                            null
                        },
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            // Task content
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    text = subTask.name,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )

                                subTask.description?.let { description ->
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White.copy(alpha = 0.7f),
                                        ),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 8.dp),
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }

                            // Done Button or Status
                            if (subTask.isCompleted) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color(0xFF10B981).copy(alpha = 0.2f),
                                            RoundedCornerShape(16.dp),
                                        )
                                        .padding(vertical = 12.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Completed",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Text(
                                        text = "Completed",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color(0xFF10B981),
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                        modifier = Modifier.padding(start = 8.dp),
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.markSubTaskDone(subTask.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NiyamColors.blueColor,
                                    ),
                                ) {
                                    Text(
                                        text = "Mark Done",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            // Control Buttons
            if (state.isFlexible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    when (state.timerState) {
                        TimerState.IDLE -> {
                            // Start Button (Play)
                            FloatingActionButton(
                                onClick = { viewModel.start() },
                                modifier = Modifier.size(64.dp),
                                containerColor = Color(0xFF10B981), // Green
                                contentColor = Color.White,
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_media_play),
                                    contentDescription = "Start",
                                    modifier = Modifier.size(32.dp),
                                )
                            }
                        }

                        TimerState.RUNNING -> {
                            // Pause Button
                            FloatingActionButton(
                                onClick = { viewModel.pause() },
                                modifier = Modifier.size(64.dp),
                                containerColor = Color(0xFFEF4444), // Red
                                contentColor = Color.White,
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_media_pause),
                                    contentDescription = "Pause",
                                    modifier = Modifier.size(32.dp),
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Done Button
                            Button(
                                onClick = { viewModel.done() },
                                modifier = Modifier
                                    .height(64.dp)
                                    .weight(1f),
                                shape = RoundedCornerShape(32.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6366F1),
                                ),
                            ) {
                                Text(
                                    text = "Done",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                )
                            }
                        }

                        TimerState.PAUSED -> {
                            // Resume Button (Play)
                            FloatingActionButton(
                                onClick = { viewModel.resume() },
                                modifier = Modifier.size(64.dp),
                                containerColor = Color(0xFF10B981), // Green
                                contentColor = Color.White,
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_media_play),
                                    contentDescription = "Resume",
                                    modifier = Modifier.size(32.dp),
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Done Button
                            Button(
                                onClick = { viewModel.done() },
                                modifier = Modifier
                                    .height(64.dp)
                                    .weight(1f),
                                shape = RoundedCornerShape(32.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6366F1),
                                ),
                            ) {
                                Text(
                                    text = "Done",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                )
                            }
                        }

                        TimerState.DONE -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF10B981).copy(alpha = 0.2f),
                                ),
                            ) {
                                Text(
                                    text = "Task Completed! 🎉",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    modifier = Modifier.padding(24.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
