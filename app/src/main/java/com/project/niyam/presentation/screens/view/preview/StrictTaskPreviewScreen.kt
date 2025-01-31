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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.project.niyam.domain.services.StrictTaskService
import com.project.niyam.domain.services.StrictTaskState
import com.project.niyam.presentation.screens.viewmodels.preview.PreviewScreenViewModel
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PreviewScreen(
    stopWatchService: StrictTaskService,
    viewModel: PreviewScreenViewModel = hiltViewModel(),
    id: Int,
) {
    val context = LocalContext.current
    val hours: String = stopWatchService.hours.value
    val minutes = stopWatchService.minutes.value
    val seconds = stopWatchService.seconds.value
    val uiState by viewModel.uiState

    viewModel.updateID(id, context)

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
                .weight(1f),
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
                // Our page content
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
                            runBlocking {
                                viewModel.subTaskDone(page)
                                var check = true
                                uiState.subTasks.forEach {
                                    if (!it.isCompleted) {
                                        check = false
                                    }
                                }
                                if (check) {
                                    viewModel.subTaskDone(
                                        page,
                                        last = true,
                                    )
                                    ServiceHelper.triggerStrictTaskService(
                                        context = context,
                                        StrictTaskState.COMPLETED.name,
                                    )
                                }
                            }
                        },
                        enabled = !uiState.subTasks[page].isCompleted,
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
        Spacer(
            modifier = Modifier
                .fillMaxHeight(0.1f)
                .weight(0.2f),
        )
    }
}
