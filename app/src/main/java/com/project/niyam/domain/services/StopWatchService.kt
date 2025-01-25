package com.project.niyam.domain.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.project.niyam.data.TaskNotification
import com.project.niyam.domain.repository.TaskRepository
import com.project.niyam.utils.Constants.ACTION_SERVICE_CANCEL
import com.project.niyam.utils.Constants.ACTION_SERVICE_START
import com.project.niyam.utils.Constants.ACTION_SERVICE_STOP
import com.project.niyam.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.project.niyam.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.project.niyam.utils.Constants.NOTIFICATION_ID
import com.project.niyam.utils.Constants.STOPWATCH_STATE
import com.project.niyam.utils.convertMinutesToHHMM
import com.project.niyam.utils.formatTime
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
    lateinit var notificationManager: NotificationManager

    @Inject
    @TaskNotification
    lateinit var notificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var taskRepository: TaskRepository


    private val binder = StopwatchBinder()
    private lateinit var timer: Timer
    private lateinit var endTime: String
    private lateinit var id: String
    private var duration: Duration = Duration.ZERO

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

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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

//                        if (currentState.value == StopwatchState.Idle)
                        startStopwatch { hours, minutes, seconds ->
                            updateNotification(hours, minutes, seconds)
                        }
                        setStartNotification()
                    }

                    StopwatchState.Stopped.name -> {
                        stopStopwatch()
                    }

                    StopwatchState.Canceled.name -> {
                        cancelStopwatch()
                        stopForegroundService()
                    }
                }

            }
        }
        return START_STICKY
    }

    private fun setStartNotification() {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.clearActions()
                .addAction(0, "Pause", ServiceHelper.stopPendingIntent(this))
                .addAction(0, "Cancel", ServiceHelper.cancelPendingIntent(this)).build(),
        )
    }

    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun fetchEndTime() {
        endTime = taskRepository.getEndTime(id.toInt()).first()
        endTime = convertMinutesToHHMM(endTime.toInt())
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startForegroundService() {
        createNotificationChannel()
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notificationBuilder.setContentIntent(
                ServiceHelper.clickPendingIntent(this, id)
            ).clearActions()
                .addAction(0, "Start", ServiceHelper.resumePendingIntent(this))
                .addAction(0, "Cancel", ServiceHelper.cancelPendingIntent(this)).build(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startStopwatch(onTick: (h: String, m: String, s: String) -> Unit) {
//        duration = calculateDifference()
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.minus(1.seconds)
            if (duration.isNegative()) {
                stopStopwatch()
                cancelStopwatch()
            }
            updateTimeUnits()
            onTick(hours.value, minutes.value, seconds.value)
        }
        currentState.value = StopwatchState.Started
    }

    private fun stopStopwatch() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.clearActions()
                .addAction(0, "Resume", ServiceHelper.resumePendingIntent(this))
                .addAction(0, "Cancel", ServiceHelper.cancelPendingIntent(this)).build(),
        )
        currentState.value = StopwatchState.Stopped
    }

    private fun cancelStopwatch() {
//        duration = Duration.ZERO
        currentState.value = StopwatchState.Idle
        updateTimeUnits()
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDifference(): Duration {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val providedTime = LocalTime.parse(endTime, formatter)
        val currentTime = LocalTime.MIDNIGHT

        return ChronoUnit.SECONDS.between(currentTime, providedTime).seconds
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateTimeUnits() {
        duration.toComponents { hours, minutes, seconds, _ ->
            this@StopWatchService.hours.value = hours.toInt().pad()
            this@StopWatchService.minutes.value = minutes.pad()
            this@StopWatchService.seconds.value = seconds.pad()
        }
    }

    private fun updateNotification(hours: String, minutes: String, seconds: String) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText("$hours:$minutes:$seconds").build()
        )
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

    //    Resumed,
    Entered
}