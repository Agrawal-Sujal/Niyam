package com.project.niyam.presentation.screens.view.preview

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.domain.services.ServiceHelper
import com.project.niyam.domain.services.StopWatchService
import com.project.niyam.domain.services.StopwatchState
import com.project.niyam.presentation.screens.viewmodels.preview.TaskPreviewScreenViewModel
import com.project.niyam.utils.Constants.ACTION_SERVICE_CANCEL
import com.project.niyam.utils.Constants.ACTION_SERVICE_START
import com.project.niyam.utils.Constants.ACTION_SERVICE_STOP

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskPreviewScreen(
    stopWatchService: StopWatchService,
    viewModel: TaskPreviewScreenViewModel = hiltViewModel(),
    id: Int,
) {
    val context = LocalContext.current
    val hours by stopWatchService.hours
    val minutes by stopWatchService.minutes
    val seconds by stopWatchService.seconds
    val currentState by stopWatchService.currentState
    val uiState by viewModel.uiState
//    val subTasks = uiState.subTasks
//    val currentIndex = uiState.currentIndex
//    if (uiState.isEnabled) {
//        viewModel.getStrictTask(id)
//        viewModel.updateEnable()
//    }
    viewModel.updateID(id)
//    viewModel.updateIsStrict(isStrict, context)
//    LaunchedEffect(key1 = id) {
//        viewModel.getStrictTask(id)
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            if (uiState.currentIndex != 0) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            viewModel.decreaseIndex()
                        },
                )
            }
            Column {
                Text(uiState.subTasks[uiState.currentIndex].subTaskName)
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = uiState.subTasks[uiState.currentIndex].subTaskDescription,
                )
            }
            if (uiState.currentIndex != uiState.subTasks.size - 1) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            viewModel.increaseIndex()
                        },
                )
            }
        }
        Button(onClick = {
            viewModel.subTaskDone()
        }, enabled = !uiState.subTasks[uiState.currentIndex].isCompleted) {
            Text(
                text = "Done",
                color = if (uiState.subTasks[uiState.currentIndex].isCompleted) Color.Green else Color.Black,
            )
        }

        Column(
            modifier = Modifier.weight(weight = 9f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
//            AnimatedContent(targetState = hours, transitionSpec = { addAnimation() }) {
            Text(
                text = hours,
                style = TextStyle(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (hours == "00") Color.White else Blue,
                ),
            )
//            }
//            AnimatedContent(targetState = minutes, transitionSpec = { addAnimation() }) {
            Text(
                text = minutes,
                style = TextStyle(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (minutes == "00") Color.White else Blue,
                ),
            )
//            }
//            AnimatedContent(targetState = seconds, transitionSpec = { addAnimation() }) {
            Text(
                text = seconds,
                style = TextStyle(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (seconds == "00") Color.White else Blue,
                ),
            )
//            }
        }
        Row(modifier = Modifier.weight(weight = 1f)) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.8f),
                onClick = {
                    ServiceHelper.triggerForegroundService(
                        context = context,
                        msg = if (currentState == StopwatchState.Started) {
                            StopwatchState.Stopped.name
                        } else {
                            StopwatchState.Started.name
                        },
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentState == StopwatchState.Started) Red else Blue,
                    contentColor = Color.White,
                ),

                ) {
                Text(
                    text = if (currentState == StopwatchState.Started) {
                        "Pause"
                    } else if ((currentState == StopwatchState.Stopped)) {
                        "Resume"
                    } else {
                        "Start"
                    },
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.8f),
                onClick = {
                    ServiceHelper.triggerForegroundService(
                        context = context,
                        msg = StopwatchState.Canceled.name,
                    )
                },
                enabled = seconds != "00" && currentState != StopwatchState.Started,
                colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.Blue),
            ) {
                Text(text = "Done for now")
            }
        }
    }
}

@ExperimentalAnimationApi
fun addAnimation(duration: Int = 600): ContentTransform {
    return (
            slideInVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeIn(
                animationSpec = tween(durationMillis = duration),
            )
            ).togetherWith(
            slideOutVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeOut(
                animationSpec = tween(durationMillis = duration),
            ),
        )
}