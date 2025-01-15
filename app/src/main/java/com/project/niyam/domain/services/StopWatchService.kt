package com.project.niyam.domain.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.project.niyam.utils.Constants.ACTION_SERVICE_CANCEL
import com.project.niyam.utils.Constants.ACTION_SERVICE_START
import com.project.niyam.utils.Constants.ACTION_SERVICE_STOP
import com.project.niyam.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.project.niyam.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.project.niyam.utils.Constants.NOTIFICATION_ID
import com.project.niyam.utils.Constants.STOPWATCH_STATE
import com.project.niyam.utils.formatTime
import com.project.niyam.utils.pad
import dagger.hilt.android.AndroidEntryPoint
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
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = StopwatchBinder()
    private lateinit var endTime: String
    private lateinit var id: String
    private var duration: Duration = Duration.parse("PT" + "60" + "S")
    private lateinit var timer: Timer

    var seconds = mutableStateOf("00")
        private set
    var minutes = mutableStateOf("00")
        private set
    var hours = mutableStateOf("00")
        private set
    var currentState = mutableStateOf(StopwatchState.Idle)
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBind(intent: Intent?): Binder {
        if (intent != null) {
            if (intent.action == "subTaskPreview") {
                endTime = intent.getStringExtra("endTime")!!
                id = intent.getStringExtra("id")!!
            }
        }
        return binder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//                endTime = intent.getStringExtra("endTime")!!
//                endTime = giveDifference(endTime)
//        duration = Duration.parse("PT" + endTime + "S")
//        Log.d("Niyam", endTime)
        when (intent?.getStringExtra(STOPWATCH_STATE)) {
            StopwatchState.Started.name -> {
                setStopButton()
                startForegroundService()
                startStopwatch { hours, minutes, seconds ->
                    updateNotification(hours = hours, minutes = minutes, seconds = seconds)
                }
            }

            StopwatchState.Stopped.name -> {
                stopStopwatch()
                setResumeButton()
            }

            StopwatchState.Canceled.name -> {
                stopStopwatch()
                cancelStopwatch()
                stopForegroundService()
            }
        }

        intent?.action.let {
            when (it) {
                ACTION_SERVICE_START -> {
                    setStopButton()
                    startForegroundService()
                    startStopwatch { hours, minutes, seconds ->
                        updateNotification(hours = hours, minutes = minutes, seconds = seconds)
                    }
                }

                ACTION_SERVICE_STOP -> {
                    stopStopwatch()
                    setResumeButton()
                }

                ACTION_SERVICE_CANCEL -> {
                    stopStopwatch()
                    cancelStopwatch()
                    stopForegroundService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startStopwatch(onTick: (h: String, m: String, s: String) -> Unit) {
        duration = giveDifference()
        currentState.value = StopwatchState.Started
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.minus(1.seconds)
            updateTimeUnits()
            onTick(hours.value, minutes.value, seconds.value)
        }
    }

    private fun stopStopwatch() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        currentState.value = StopwatchState.Stopped
    }

    private fun cancelStopwatch() {
        duration = Duration.ZERO
        currentState.value = StopwatchState.Idle
        updateTimeUnits()
    }

    private fun updateTimeUnits() {
        duration.toComponents { hours, minutes, seconds, _ ->
            this@StopWatchService.hours.value = hours.toInt().pad()
            this@StopWatchService.minutes.value = minutes.pad()
            this@StopWatchService.seconds.value = seconds.pad()
        }
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
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

    private fun updateNotification(hours: String, minutes: String, seconds: String) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText(
                formatTime(
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                ),
            ).setContentIntent(
                ServiceHelper.clickPendingIntent(
                    this,
                    id = id,
                    endTime = endTime,
                ),
            ).build(),
        )
    }

    private fun setStopButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                "Stop",
                ServiceHelper.stopPendingIntent(this),
            ),
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun setResumeButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                "Resume",
                ServiceHelper.resumePendingIntent(this),
            ),
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun giveDifference(): Duration {
        val providedTimeString = endTime

        // Define the formatter for the string format
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        // Parse the string into a LocalTime object
        val providedTime = LocalTime.parse(providedTimeString, formatter)

        // Get the current time
        val currentTime = LocalTime.now()

        // Calculate the difference
        val duration1 = ChronoUnit.SECONDS.between(currentTime, providedTime)
        return Duration.parse("PT" + duration1 + "S")
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
}
