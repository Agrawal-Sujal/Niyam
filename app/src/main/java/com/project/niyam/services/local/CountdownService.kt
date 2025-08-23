package com.project.niyam.services.local

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationManagerCompat
import com.project.niyam.data.local.entity.AlarmEntity
import com.project.niyam.domain.repository.AlarmRepository
import com.project.niyam.utils.NotificationHelper
import com.project.niyam.utils.TimerState
import com.project.niyam.utils.secondsUntil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

@AndroidEntryPoint
class CountdownService : Service() {

    @Inject
    lateinit var repo: AlarmRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var tickJob: Job? = null
    private var currentId: Long? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getLongExtra(EXTRA_ALARM_ID, -1L) ?: -1L
        if (id == -1L) return START_NOT_STICKY
        currentId = id
        when (intent?.action) {
            ACTION_BOOT -> initIdleIfMissing(id)
            ACTION_START -> startCountdown(id)
            ACTION_RESUME -> resumeCountdown(id)
            ACTION_PAUSE -> pauseCountdown(id)
            ACTION_DONE -> doneCountdown(id)
            else -> updateNotification(id) // keep UI fresh
        }
        return START_STICKY
    }

    override fun onDestroy() {
        tickJob?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // region Actions
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun initIdleIfMissing(id: Long) = scope.launch {
        val a = repo.get(id) ?: return@launch
        // ensure we show a notification even if user started service “cold”
        postOrUpdate(a)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun startCountdown(id: Long) = scope.launch {
        val a = repo.get(id) ?: return@launch
        val start = when (a.state) {
            TimerState.IDLE, TimerState.PAUSED, TimerState.RUNNING -> a
            TimerState.DONE -> a.copy(state = TimerState.DONE, secondsRemaining = 0)
        }
        val normalized = if (start.secondsRemaining <= 0) {
            start.copy(state = TimerState.DONE, secondsRemaining = 0)
        } else {
            start.copy(state = TimerState.RUNNING)
        }

        repo.save(normalized)
        postOrUpdate(normalized)
        beginTicking(normalized.id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun resumeCountdown(id: Long) = startCountdown(id)

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun pauseCountdown(id: Long) = scope.launch {
        tickJob?.cancel()
        val a = repo.get(id) ?: return@launch
        val paused = a.copy(state = TimerState.PAUSED)
        repo.save(paused)
        postOrUpdate(paused)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun doneCountdown(id: Long) = scope.launch {
        tickJob?.cancel()
        val a = repo.get(id) ?: return@launch
        val done = a.copy(state = TimerState.IDLE)
        repo.save(done)
        postOrUpdate(done)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    // endregion
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun beginTicking(id: Long) {
        tickJob?.cancel()
        tickJob = scope.launch {
            while (isActive) {
                delay(1000)
                val a = repo.get(id) ?: break
                if (a.state != TimerState.RUNNING) break
                val endDate = a.endDate
                val endTime = a.endTime
                val isFlexible = a.isFlexible
                var next: Long = (a.secondsRemaining - 1).coerceAtLeast(0).toLong()
                next = if (isFlexible) {
                    min(next, secondsUntil(endDate, endTime))
                } else {
                    secondsUntil(endDate, endTime)
                }
                if (next < 0) next = 0
                Log.d("CountdownService", "next: $next")
                val updated = a.copy(
                    secondsRemaining = next.toInt(),
                    state = if (next.toInt() == 0) TimerState.DONE else TimerState.RUNNING,
                )
                repo.save(updated)
                postOrUpdate(updated)

                if (updated.state == TimerState.DONE) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    break
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun postOrUpdate(alarm: AlarmEntity) {
        val notifId = alarm.id.toInt()
        val notification = NotificationHelper.build(
            this,
            alarm,
        ) { action -> pendingToService(this, alarm.id, action) }

        if (alarm.state == TimerState.RUNNING && currentForegroundId != notifId) {
            startForeground(notifId, notification)
            currentForegroundId = notifId
        } else {
            NotificationManagerCompat.from(this).notify(notifId, notification)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun updateNotification(id: Long) = scope.launch {
        repo.get(id)?.let { postOrUpdate(it) }
    }

    companion object {
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val ACTION_BOOT = "countdown.BOOT"
        const val ACTION_START = "countdown.START"
        const val ACTION_PAUSE = "countdown.PAUSE"
        const val ACTION_RESUME = "countdown.RESUME"
        const val ACTION_DONE = "countdown.DONE"

        private var currentForegroundId: Int? = null

        fun intent(ctx: Context, id: Long, action: String): Intent =
            Intent(ctx, CountdownService::class.java).apply {
                this.action = action
                putExtra(EXTRA_ALARM_ID, id)
            }

        fun pendingToService(
            ctx: Context,
            id: Long,
            action: String,
        ): PendingIntent =
            PendingIntent.getService(
                ctx,
                (id.toInt() shl 8) + action.hashCode(),
                intent(ctx, id, action),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
    }
}
