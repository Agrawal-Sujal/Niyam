package com.project.niyam.domain.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.project.niyam.data.TaskNotification
import com.project.niyam.domain.model.GeneralInfo
import com.project.niyam.domain.model.Tasks
import com.project.niyam.domain.repository.GeneralInfoRepository
import com.project.niyam.domain.repository.TaskRepository
import com.project.niyam.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.project.niyam.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.project.niyam.utils.Constants.NOTIFICATION_ID
import com.project.niyam.utils.Constants.STOPWATCH_STATE
import com.project.niyam.utils.convertSecondsToTime
import com.project.niyam.utils.pad
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class StopWatchService : Service() {
    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    @TaskNotification
    lateinit var notificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var generalInfoRepository: GeneralInfoRepository

    private val binder = StopwatchBinder()
    private lateinit var timer: Timer
    private var task: Tasks = Tasks()
    private var endTime: String? = ""
    private lateinit var id: String
    var duration: Duration = Duration.ZERO

    var seconds = mutableStateOf("00")
        private set
    var minutes = mutableStateOf("00")
        private set
    var hours = mutableStateOf("00")
        private set
    var currentState = mutableStateOf(StopwatchState.Idle)
        private set
    override fun onBind(intent: Intent?): Binder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        CoroutineScope(Dispatchers.IO).launch {
            if (intent != null && intent.action == "subTaskPreview") {
                when (intent.getStringExtra(STOPWATCH_STATE)) {
                    StopwatchState.Entered.name -> {
                        if (currentState.value == StopwatchState.Idle) {
                            id = intent.getStringExtra("id") ?: "0"
                            fetchEndTime()
                            startForegroundService()
                            duration = calculateDifference()
                            updateTimeUnits()
                        }
                    }

                    StopwatchState.Started.name -> {
                        startForegroundService()
//                        if (currentState.value == StopwatchState.Idle)
                        startStopwatch { hours, minutes, seconds ->
                        }
                    }

                    StopwatchState.Stopped.name -> {
                        stopStopwatch()
                    }

                    StopwatchState.Canceled.name -> {
                        stopStopwatch()
                        updateEndTime()
                        cancelStopwatch()
                    }

                    StopwatchState.Completed.name -> {
//                        duration = Duration.ZERO
                        stopStopwatch()
                    }
                }
            }
        }
        return START_STICKY
    }

    private suspend fun updateEndTime() {
        task = taskRepository.getTaskById(id.toInt()).first()
//        val isCompleted: Boolean = duration == Duration.ZERO
        taskRepository.updateTasks(
            task.copy(
                secondsRemaining = duration.inWholeSeconds.toString(),
            ),
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startForegroundService() {
        createNotificationChannel()
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notificationBuilder.build(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST,
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW,
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun fetchEndTime() {
        task = taskRepository.getTaskById(id.toInt()).first()
        endTime = task.secondsRemaining
        endTime = convertSecondsToTime(endTime!!.toInt())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startStopwatch(onTick: (h: String, m: String, s: String) -> Unit) {
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.minus(1.seconds)
            if (duration.isNegative()) {
                currentState.value = StopwatchState.Completed
                CoroutineScope(Dispatchers.IO).launch {
                    generalInfoRepository.updateGeneralInfo(
                        GeneralInfo(
                            normalTaskRunningId = 0,
                            strictTaskRunningId = 0,
                        ),
                    )
                    task = taskRepository.getTaskById(id.toInt()).first()
                    taskRepository.updateTasks(task.copy(isCompleted = -1))
                }
                duration = Duration.ZERO
                stopStopwatch()
//                cancelStopwatch()
//                ServiceHelper.triggerForegroundService(
//                    this@StopWatchService,
//                    StopwatchState.Canceled.name,
//                )
            } else {
                currentState.value = StopwatchState.Started
                updateTimeUnits()
                onTick(hours.value, minutes.value, seconds.value)
            }
        }
    }

    private fun stopStopwatch() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        notificationManager.cancel(NOTIFICATION_ID)
//        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        currentState.value = StopwatchState.Completed
    }

    private fun cancelStopwatch() {
        currentState.value = StopwatchState.Idle
        updateTimeUnits()
//        stopForeground(STOP_FOREGROUND_REMOVE)
//        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDifference(): Duration {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val providedTime = LocalTime.parse(endTime, formatter)
        val currentTime = LocalTime.MIDNIGHT

        return ChronoUnit.SECONDS.between(currentTime, providedTime).seconds
    }

    private fun updateTimeUnits() {
        duration.toComponents { hours, minutes, seconds, _ ->
            this@StopWatchService.hours.value = hours.toInt().pad()
            this@StopWatchService.minutes.value = minutes.pad()
            this@StopWatchService.seconds.value = seconds.pad()
        }
    }

    inner class StopwatchBinder : Binder() {
        fun getService(): StopWatchService = this@StopWatchService
    }
}

enum class StopwatchState {
    Idle,
    Started,
    Stopped,
    Canceled,
    Entered,
    Completed,
}
