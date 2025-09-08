package com.project.niyam.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.project.niyam.R
import com.project.niyam.data.local.entity.AlarmEntity
import com.project.niyam.services.local.CountdownService

object NotificationHelper {

    fun build(
        ctx: Context,
        alarm: AlarmEntity,
        toService: (String) -> PendingIntent,
    ): Notification {
        val (title, text) = when (alarm.state) {
            TimerState.RUNNING -> "Running" to format(alarm.secondsRemaining)
            TimerState.PAUSED -> "Paused" to format(alarm.secondsRemaining)
            TimerState.IDLE -> "Ready" to format(alarm.secondsRemaining)
            TimerState.DONE -> "Done" to "Completed"
        }
        val isFlexible = alarm.isFlexible
        val builder = NotificationCompat.Builder(ctx, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Alarm #${alarm.id} • $title")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setOnlyAlertOnce(true)
            .setOngoing(alarm.state == TimerState.RUNNING || alarm.state == TimerState.PAUSED)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        // State-aware actions
        if (isFlexible) {
            when (alarm.state) {
                TimerState.IDLE -> {
                    builder.addAction(0, "Start", toService(CountdownService.ACTION_START))
                    builder.addAction(0, "Done", toService(CountdownService.ACTION_DONE))
                }

                TimerState.RUNNING -> {
                    builder.addAction(0, "Pause", toService(CountdownService.ACTION_PAUSE))
                    builder.addAction(0, "Done", toService(CountdownService.ACTION_DONE))
                }

                TimerState.PAUSED -> {
                    builder.addAction(0, "Resume", toService(CountdownService.ACTION_RESUME))
                    builder.addAction(0, "Done", toService(CountdownService.ACTION_DONE))
                }

                TimerState.DONE -> {
                    // No actions needed
                }
            }
        }

        return builder.build()
    }

    fun buildLoadingNotification(ctx: Context): Notification {
        return NotificationCompat.Builder(ctx, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Starting alarm service…")
            .setContentText("Loading, please wait")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    @SuppressLint("DefaultLocale")
    private fun format(totalSec: Int): String {
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = totalSec % 60
        return if (h > 0) {
            String.format("%d:%02d:%02d", h, m, s)
        } else {
            String.format("%d:%02d", m, s)
        }
    }
}
