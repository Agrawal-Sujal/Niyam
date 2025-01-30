package com.project.niyam.presentation.screens.view.preview

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.R
import com.project.niyam.domain.services.ServiceHelper
import com.project.niyam.domain.services.StopWatchService
import com.project.niyam.domain.services.StopwatchState
import com.project.niyam.presentation.screens.viewmodels.preview.TaskPreviewScreenViewModel

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
    if (hours == "00" && minutes == "00" && seconds == "00") {
        LaunchedEffect(key1 = id) {
            viewModel.offPrefUtil()
        }
    }
    viewModel.updateID(id)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (hours != "00") {
                Text(
                    text = hours,
                    style = TextStyle(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.NormalText),
                    ),
                )
                Text(":", fontSize = 64.sp, color = colorResource(R.color.NormalText))
            }
            if (minutes != "00") {
                Text(
                    text = minutes,
                    style = TextStyle(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.NormalText),
                    ),
                )
                Text(":", fontSize = 64.sp, color = colorResource(R.color.NormalText))
            }
            Text(
                text = seconds,
                style = TextStyle(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.NormalText),
                ),
            )
        }

        Spacer(
            modifier = Modifier
                .height(12.dp)
                .weight(0.15f),
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(0.7f),
            colors = CardColors(
                contentColor = colorResource(R.color.NormalText),
                containerColor = colorResource(R.color.PrimaryColor),
                disabledContainerColor = colorResource(R.color.PrimaryColor),
                disabledContentColor = colorResource(R.color.PrimaryColor),
            ),
        ) {
            val pagerState = rememberPagerState(pageCount = {
                uiState.subTasks.size
            })
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            ) { page ->
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        uiState.subTasks[page].subTaskName,
                        fontSize = 24.sp,
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = uiState.subTasks[page].subTaskDescription,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            viewModel.subTaskDone(page)
                            var check = true
                            uiState.subTasks.forEach {
                                if (!it.isCompleted) {
                                    check = false
                                }
                            }
                            if (check) {
                                viewModel.updateComplete()
                                ServiceHelper.triggerForegroundService(
                                    context = context,
                                    StopwatchState.Completed.name,
                                )
                            }
                        },
                        enabled = currentState == StopwatchState.Started,
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    ) {
                        Text(
                            text = "Done",
                            color = if (uiState.subTasks[page].isCompleted) Color.Green else Color.Black,
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        if (hours != "00" || minutes != "00" || seconds != "00") {
            Row(modifier = Modifier.weight(0.15f)) {
                Button(
                    enabled = currentState != StopwatchState.Started,
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight(0.8f),
                    onClick = {
                        viewModel.onPrefUtil(id)
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            msg = StopwatchState.Started.name,
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentState == StopwatchState.Started) colorResource(R.color.PrimaryColorText) else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = colorResource(R.color.NormalText),
                    ),
                ) {
                    Text(
                        text = "Start",
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight(0.8f),
                    onClick = {
                        viewModel.offPrefUtil()
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            msg = StopwatchState.Canceled.name,
                        )
                    },
                    enabled = currentState == StopwatchState.Started,
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = colorResource(R.color.PrimaryColor),
                        containerColor = colorResource(R.color.PrimaryColorText),
                    ),
                ) {
                    Text(text = "Done for now")
                }
            }
        }
    }
    Spacer(
        modifier = Modifier
            .fillMaxHeight(0.1f),
    )
}
