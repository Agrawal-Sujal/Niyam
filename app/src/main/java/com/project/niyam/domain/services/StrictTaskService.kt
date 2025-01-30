package com.project.niyam.domain.services

import android.annotation.SuppressLint
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
import com.project.niyam.data.StrictTaskNotification
import com.project.niyam.domain.model.StrictTasks
import com.project.niyam.domain.repository.StrictTaskRepository
import com.project.niyam.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.project.niyam.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.project.niyam.utils.Constants.NOTIFICATION_ID
import com.project.niyam.utils.Constants.PREF_UTILS_TASK
import com.project.niyam.utils.Constants.STRICT_TASK_STATE
import com.project.niyam.utils.PrefUtils
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
class StrictTaskService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var strictTaskRepository: StrictTaskRepository

    @Inject
    lateinit var prefUtils: PrefUtils

    @Inject
    @StrictTaskNotification
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = StrictTaskBinder()
    var endTime: String? = null
    var hours = mutableStateOf("00")
    var minutes = mutableStateOf("00")
    var seconds = mutableStateOf("00")
    private lateinit var timer: Timer
    private var duration = Duration.ZERO
    private var id: String? = null
    var task: StrictTasks = StrictTasks()

    var currentState = mutableStateOf(StrictTaskState.IDLE)

    override fun onBind(intent: Intent?): Binder = binder

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("OnStartCommand", "Hii")
        CoroutineScope(Dispatchers.IO).launch {
            if (intent != null && intent.action == "subTaskPreview") {
                if (intent.getStringExtra(STRICT_TASK_STATE) == StrictTaskState.COMPLETED.name) {
                    duration = Duration.ZERO
//                    strictTaskRepository.updateStrictTasks(task.copy(isCompleted = true))
                    stopStrictTask()
                } else {
                    id = intent.getStringExtra("id") ?: "0"
                    Log.d("OnStartCommand", id.toString())
                    if (currentState.value == StrictTaskState.IDLE || currentState.value == StrictTaskState.COMPLETED) {
                        fetchEndTime()
                        Log.d("OnStartCommand3", endTime.toString())
                        startForegroundService()
                        currentState.value = StrictTaskState.STARTED
                    }
                }
            }
        }
        return START_STICKY
    }

    //    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startForegroundService() {

        createNotificationChannel()
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notificationBuilder.setContentIntent(ServiceHelper.strictClickPendingIntent(this, id!!))
                .build(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
        )
        startStopwatch { hours, minutes, seconds ->
            updateNotification(hours, minutes, seconds)
        }
//        startForeground(NOTIFICATION_ID, notificationBuilder.build())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startStopwatch(
        onTick: (h: String, m: String, s: String) -> Unit
    ) {
        duration = calculateDifference()
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.minus(1.seconds)

            if (duration.isNegative()) {
                CoroutineScope(Dispatchers.IO).launch {
                    prefUtils.saveString(PREF_UTILS_TASK, "0")
                    strictTaskRepository.updateStrictTasks(task.copy(isCompleted = true))
                }
                stopStrictTask()
            } else {
                updateTimeUnits()
                onTick(hours.value, minutes.value, seconds.value)
            }
        }
        currentState.value = StrictTaskState.STARTED
    }

    private fun stopStrictTask() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        currentState.value = StrictTaskState.COMPLETED
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        duration = Duration.ZERO
        updateTimeUnits()
    }

    private fun updateNotification(hours: String, minutes: String, seconds: String) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText("$hours:$minutes:$seconds").build()
        )
    }

    private fun updateTimeUnits() {
        duration.toComponents { hours, minutes, seconds, _ ->
            this.hours.value = hours.toInt().pad()
            this.minutes.value = minutes.pad()
            this.seconds.value = seconds.pad()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun fetchEndTime() {
        endTime = strictTaskRepository.getEndTime(id!!.toInt()).first()
        task = strictTaskRepository.getStrictTaskById(id!!.toInt()).first()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDifference(): Duration {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val endTime = LocalTime.parse(endTime, formatter)
        val currentTime = LocalTime.now()
        return ChronoUnit.SECONDS.between(currentTime, endTime).seconds
    }

    inner class StrictTaskBinder : Binder() {
        fun getService(): StrictTaskService = this@StrictTaskService
    }
}

enum class StrictTaskState {
    IDLE,
    STARTED,
    COMPLETED
}